package org.voegtle.weatherstation.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.voegtle.weatherstation.client.util.StringUtil;
import org.voegtle.weatherstation.server.logic.WeatherDataSmoother;
import org.voegtle.weatherstation.server.parser.DataLine;
import org.voegtle.weatherstation.server.parser.DataParser;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.request.IncomingUrlParameter;
import org.voegtle.weatherstation.server.request.ResponseCode;
import org.voegtle.weatherstation.server.util.HashService;

public class IncomingServlet extends AbstractServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result;

    IncomingUrlParameter param = new IncomingUrlParameter(request);
    if (isSecretValid(param.getSecret())) {
      if (isCorrectLocation(param.getLocation())) {
        if (param.getData() != null) {
          DataLine dataLine = new DataLine(param.getData());
          DataParser parser = new DataParser();
          WeatherDataSet dataSet = parser.parse(dataLine);
          boolean persisted = pm.makePersitant(dataSet);
          if (persisted) {
            new WeatherDataSmoother(pm).smoothWeatherData();
          }

          result = persisted ? ResponseCode.ACKNOWLEDGE : ResponseCode.IGNORED;
        } else {
          result = ResponseCode.EMPTY;
        }
      } else {
        result = ResponseCode.WRONG_LOCATION;
      }
    } else {
      result = ResponseCode.NOT_AUTHORIZED;
    }

    returnResult(response, result);
  }

}

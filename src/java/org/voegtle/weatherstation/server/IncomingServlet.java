package org.voegtle.weatherstation.server;

import org.voegtle.weatherstation.server.logic.WeatherDataImporter;
import org.voegtle.weatherstation.server.parser.DataLine;
import org.voegtle.weatherstation.server.request.IncomingUrlParameter;
import org.voegtle.weatherstation.server.request.ResponseCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IncomingServlet extends AbstractServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result;

    IncomingUrlParameter param = new IncomingUrlParameter(request);
    if (isSecretValid(param.getSecret())) {
      if (isCorrectLocation(param.getLocation())) {
        if (param.getData() != null) {
          WeatherDataImporter importer = new WeatherDataImporter(pm);
          result = importer.doImport(new DataLine(param.getData()));
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

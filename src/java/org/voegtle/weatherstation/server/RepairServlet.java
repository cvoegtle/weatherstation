package org.voegtle.weatherstation.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.voegtle.weatherstation.server.logic.WeatherDataRepair;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.request.RepairUrlParameter;
import org.voegtle.weatherstation.server.request.ResponseCode;
import org.voegtle.weatherstation.server.request.UrlParameter;

public class RepairServlet extends AbstractServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    RepairUrlParameter param = new RepairUrlParameter(request);

    if (isSecretValid(param.getSecret())) {
      WeatherDataRepair repairService = new WeatherDataRepair(pm);
      List<SmoothedWeatherDataSet> repaired = repairService.repair(param.getBegin(), param.getEnd());
      returnDetailedResult(response, repaired);
    } else {
      returnResult(response, ResponseCode.NOT_AUTHORIZED);
    }
  }

}

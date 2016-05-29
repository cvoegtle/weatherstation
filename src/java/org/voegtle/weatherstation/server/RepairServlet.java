package org.voegtle.weatherstation.server;

import org.voegtle.weatherstation.server.logic.WeatherDataRepair;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.request.RepairUrlParameter;
import org.voegtle.weatherstation.server.request.ResponseCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RepairServlet extends AbstractServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    RepairUrlParameter param = new RepairUrlParameter(request, locationProperties.getDateUtil());

    if (isSecretValid(param.getSecret())) {
      WeatherDataRepair repairService = new WeatherDataRepair(pm, locationProperties);
      List<SmoothedWeatherDataSet> repaired = repairService.repair(param.getBegin(), param.getEnd());
      returnDetailedResult(response, repaired, false);
    } else {
      returnResult(response, ResponseCode.NOT_AUTHORIZED);
    }
  }

}

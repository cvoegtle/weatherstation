package org.voegtle.weatherstation.server;

import org.json.JSONObject;
import org.voegtle.weatherstation.server.data.RainDTO;
import org.voegtle.weatherstation.server.data.Statistics;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.request.DataType;
import org.voegtle.weatherstation.server.request.OutgoingUrlParameter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutgoingServlet extends AbstractServlet {

  @Override
  public void init() throws ServletException {
    super.init();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Access-Control-Allow-Origin", "*");

    WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(pm, locationProperties);

    OutgoingUrlParameter param = new OutgoingUrlParameter(request, locationProperties.getDateUtil());
    boolean authorized = isReadSecretValid(param.getSecret());

    if (param.getType() == DataType.AGGREGATED) {
      List<AggregatedWeatherDataSet> result = weatherDataFetcher.getAggregatedWeatherData(param.getBegin(), param.getEnd());
      returnAggregatedResult(response, result, param.isExtended());
    } else if (param.getType() == DataType.CURRENT) {
      UnformattedWeatherDTO currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted(authorized);
      returnCurrentWeatherData(response, currentWeatherData, param.isExtended(), param.isNewFormat());
    } else if (param.getType() == DataType.RAIN) {
      RainDTO rainData = weatherDataFetcher.fetchRainData();
      writeResponse(response, jsonConverter.toJson(rainData));
    } else if (param.getType() == DataType.STATS) {
      Statistics stats = weatherDataFetcher.fetchStatistics();
      writeResponse(response, jsonConverter.toJson(stats, param.isNewFormat()));
    } else if (param.getBegin() != null) {
      List<SmoothedWeatherDataSet> result = weatherDataFetcher.fetchSmoothedWeatherData(param.getBegin(), param.getEnd());
      returnDetailedResult(response, result, authorized);
    }

  }

  private void returnCurrentWeatherData(HttpServletResponse response, UnformattedWeatherDTO currentWeatherData, boolean extended, boolean newFormat) {
    JSONObject json = newFormat ?  jsonConverter.toJson(currentWeatherData) : jsonConverter.toJsonLegacy(currentWeatherData, extended);
    writeResponse(response, json);
  }

  private void returnAggregatedResult(HttpServletResponse response, List<AggregatedWeatherDataSet> list, boolean extended) {
    ArrayList<JSONObject> jsonObjects = jsonConverter.toJsonAggregated(list, extended);

    writeResponse(response, jsonObjects);

  }

}

package org.voegtle.weatherstation.server;

import org.json.JSONObject;
import org.voegtle.weatherstation.server.data.RainDTO;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.logic.WeatherDataAggregator;
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.request.DataType;
import org.voegtle.weatherstation.server.request.OutgoingUrlParameter;
import org.voegtle.weatherstation.server.request.WeatherUrl;
import org.voegtle.weatherstation.server.util.JSONConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OutgoingServlet extends AbstractServlet {
  private static final long serialVersionUID = 1L;
  private JSONConverter jsonConverter;

  @Override
  public void init() throws ServletException {
    super.init();
    jsonConverter = new JSONConverter(locationProperties);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    WeatherDataAggregator aggregator = new WeatherDataAggregator(pm);
    aggregator.aggregateWeatherData();

    WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(pm);

    OutgoingUrlParameter param = new OutgoingUrlParameter(request);

    if (param.getType() == DataType.AGGREGATED) {
      List<AggregatedWeatherDataSet> result = weatherDataFetcher.getAggregatedWeatherData(param.getBegin(), param.getEnd());
      returnAggregatedResult(response, result);
    } else if (param.getType() == DataType.CURRENT) {
      UnformattedWeatherDTO currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted();
      returnCurrentWeatherData(response, currentWeatherData, param.isExtended());
    } else if (param.getType() == DataType.RAIN) {
      RainDTO rainData = weatherDataFetcher.fetchRainData();
      writeResponse(response, jsonConverter.toJson(rainData));
    } else if (param.getType() == DataType.ALL) {
      try {
        ArrayList<JSONObject> collectedWeatherData = new ArrayList<>();

        try {
          UnformattedWeatherDTO currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted();
          JSONObject paderbornCurrent = jsonConverter.toJson(currentWeatherData, param.isExtended());
          collectedWeatherData.add(paderbornCurrent);
        } catch (Throwable throwable) {
          // weiter machen, auch wenn wir die aktuellen Daten nicht lesen können.
        }


        JSONObject bonnCurrent = getWeatherDataFromUrl(new WeatherUrl("forstwetter.appspot.com", DataType.CURRENT, param.isExtended()).getUrl());
        collectedWeatherData.add(bonnCurrent);

        JSONObject freiburgCurrent = getWeatherDataFromUrl(new WeatherUrl("oxenwetter.appspot.com", DataType.CURRENT, param.isExtended()).getUrl());
        collectedWeatherData.add(freiburgCurrent);

        writeResponse(response, collectedWeatherData);

      } catch (Exception ignored) {
      }

    } else if (param.getBegin() != null) {
      List<SmoothedWeatherDataSet> result = weatherDataFetcher.fetchSmoothedWeatherData(param.getBegin(), param.getEnd());
      returnDetailedResult(response, result);
    }

  }

  private void returnCurrentWeatherData(HttpServletResponse response, UnformattedWeatherDTO currentWeatherData, boolean extended) {
    JSONObject json = jsonConverter.toJson(currentWeatherData, extended);
    writeResponse(response, json);
  }

  private JSONObject getWeatherDataFromUrl(URL url) throws Exception {
    StringBuilder received = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      received.append(line);
    }
    JSONObject current;
    current = new JSONObject(received.toString());
    return current;
  }

  private void returnAggregatedResult(HttpServletResponse response, List<AggregatedWeatherDataSet> list) {
    ArrayList<JSONObject> jsonObjects = jsonConverter.toJson(list);

    writeResponse(response, jsonObjects);

  }

}

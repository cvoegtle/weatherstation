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
import org.voegtle.weatherstation.server.request.WeatherUrl;

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

  @Override
  public void init() throws ServletException {
    super.init();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Access-Control-Allow-Origin", "*");

    WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(pm);

    OutgoingUrlParameter param = new OutgoingUrlParameter(request);
    boolean authorized = isReadSecretValid(param.getSecret());

    if (param.getType() == DataType.AGGREGATED) {
      List<AggregatedWeatherDataSet> result = weatherDataFetcher.getAggregatedWeatherData(param.getBegin(), param.getEnd());
      returnAggregatedResult(response, result);
    } else if (param.getType() == DataType.CURRENT) {
      UnformattedWeatherDTO currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted(authorized);
      returnCurrentWeatherData(response, currentWeatherData, param.isExtended());
    } else if (param.getType() == DataType.RAIN) {
      RainDTO rainData = weatherDataFetcher.fetchRainData();
      writeResponse(response, jsonConverter.toJson(rainData));
    } else if (param.getType() == DataType.STATS) {
      Statistics stats = weatherDataFetcher.fetchStatistics();
      writeResponse(response, jsonConverter.toJson(stats));
    } else if (param.getType() == DataType.ALL) {
      try {
        ArrayList<JSONObject> collectedWeatherData = new ArrayList<>();

        try {
          UnformattedWeatherDTO currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted(authorized);
          JSONObject paderbornCurrent = jsonConverter.toJson(currentWeatherData, param.isExtended());
          collectedWeatherData.add(paderbornCurrent);
        } catch (Throwable throwable) {
          // weiter machen, auch wenn wir die aktuellen Daten nicht lesen können.
        }


        fetchWeatherData(collectedWeatherData, new WeatherUrl("forstwetter.appspot.com", DataType.CURRENT, param.isExtended()));
        fetchWeatherData(collectedWeatherData, new WeatherUrl("oxenwetter.appspot.com", DataType.CURRENT, param.isExtended()));

        writeResponse(response, collectedWeatherData);

      } catch (Exception ex) {
        log.severe("Exception while reading weather data " + ex.toString());
      }

    } else if (param.getBegin() != null)

    {
      List<SmoothedWeatherDataSet> result = weatherDataFetcher.fetchSmoothedWeatherData(param.getBegin(), param.getEnd());
      returnDetailedResult(response, result, authorized);
    }

  }

  private void fetchWeatherData(ArrayList<JSONObject> collectedWeatherData, WeatherUrl weatherUrl) {
    try {
      JSONObject bonnCurrent = getWeatherDataFromUrl(weatherUrl.getUrl());
      collectedWeatherData.add(bonnCurrent);
    } catch (Exception ex) {
      log.severe(ex.toString());
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
    return new JSONObject(received.toString());
  }

  private void returnAggregatedResult(HttpServletResponse response, List<AggregatedWeatherDataSet> list) {
    ArrayList<JSONObject> jsonObjects = jsonConverter.toJson(list);

    writeResponse(response, jsonObjects);

  }

}

package org.voegtle.weatherstation.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherstation.client.dto.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.logic.WeatherDataAggregator;
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.request.DataType;
import org.voegtle.weatherstation.server.request.OutgoingUrlParameter;
import org.voegtle.weatherstation.server.request.WeatherUrl;
import org.voegtle.weatherstation.server.util.WeatherJSONObject;

public class OutgoingServlet extends AbstractServlet {
  private static final long serialVersionUID = 1L;


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    WeatherDataAggregator aggregator = new WeatherDataAggregator(pm);
    aggregator.aggregateWeatherData();

    WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(pm);

    OutgoingUrlParameter param = new OutgoingUrlParameter(request);

    if (param.getType() == DataType.AGGREGATED) {
      List<AggregatedWeatherDataSet> result = weatherDataFetcher.getAggregatedWeatherData(param.getBegin(), param.getEnd());
      returnAggregatedResult(response, result);
    } else if (param.getType()  == DataType.CURRENT) {
      UnformattedWeatherDTO currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted();
      returnCurrentWeatherData(response, currentWeatherData, param.isExtended());
    } else if (param.getType()  == DataType.ALL) {
      try {
        ArrayList<JSONObject> collectedWeatherData = new ArrayList<JSONObject>();

        try {
          UnformattedWeatherDTO currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted();
          JSONObject paderbornCurrent = convertToJson(currentWeatherData, param.isExtended());
          collectedWeatherData.add(paderbornCurrent);
        } catch (Throwable throwable) {
          // weiter machen, auch wenn wir die aktuellen Daten nicht lesen können.
        }


        JSONObject bonnCurrent = getWeatherDataFromUrl(new WeatherUrl("forstwetter.appspot.com", DataType.CURRENT, param.isExtended()).getUrl());
        collectedWeatherData.add(bonnCurrent);

        JSONObject freiburgCurrent = getWeatherDataFromUrl(new WeatherUrl("oxenwetter.appspot.com", DataType.CURRENT, param.isExtended()).getUrl());
        collectedWeatherData.add(freiburgCurrent);

        writeResponse(response, collectedWeatherData);

      } catch (Exception ex) {
      }

    } else if (param.getBegin() != null) {
      List<SmoothedWeatherDataSet> result = weatherDataFetcher.fetchSmoothedWeatherData(param.getBegin(), param.getEnd());
      returnDetailedResult(response, result);
    }

  }

  private void returnCurrentWeatherData(HttpServletResponse response, UnformattedWeatherDTO currentWeatherData, boolean extended) {
    JSONObject json = convertToJson(currentWeatherData, extended);
    writeResponse(response, json);
  }

  private JSONObject getWeatherDataFromUrl(URL url) throws Exception {
    StringBuffer received = new StringBuffer();
    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      received.append(line);
    }
    JSONObject current;
    current = new JSONObject(received.toString());
    return current;
  }

  private JSONObject convertToJson(UnformattedWeatherDTO currentWeatherData, boolean extended) {
    JSONObject json = new WeatherJSONObject();
    try {
      json.put("timestamp", currentWeatherData.getTime());
      json.put("temperature", currentWeatherData.getTemperature());
      json.put("humidity", currentWeatherData.getHumidity());
      json.put("rain", currentWeatherData.getRainLastHour());
      json.put("rain_today", currentWeatherData.getRainToday());
      json.put("raining", currentWeatherData.isRaining());
      json.put("wind", currentWeatherData.getWindspeed());

      json.put("location", locationProperties.getCity());
      if (extended) {
        json.put("forecast", locationProperties.getWeatherForecast());
      }
    } catch (JSONException e) {
    }
    return json;
  }

  private void returnAggregatedResult(HttpServletResponse response, List<AggregatedWeatherDataSet> list) {
    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);

    ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
    for (AggregatedWeatherDataSet wds : list) {
      JSONObject json = new WeatherJSONObject();
      try {
        json.put("date", sdf.format(wds.getDate()));
        json.put("tempAvg", wds.getOutsideTemperatureAverage());
        json.put("tempMin", wds.getOutsideTemperatureMin());
        json.put("tempMax", wds.getOutsideTemperatureMax());
        json.put("humAvg", wds.getOutsideHumidityAverage());
        json.put("humMin", wds.getOutsideHumidityMin());
        json.put("humMax", wds.getOutsideHumidityMax());
        json.put("wind", wds.getWindspeedAverage());
        json.put("windMax", wds.getWindspeedMax());
        double rain = 0.295 * (wds.getRainCounter());
        json.put("rain", Math.max(rain, 0));
      } catch (JSONException e) {
        e.printStackTrace();
      }
      jsonObjects.add(json);
    }

    writeResponse(response, jsonObjects);

  }
}

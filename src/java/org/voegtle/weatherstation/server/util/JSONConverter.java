package org.voegtle.weatherstation.server.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.data.RainDTO;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JSONConverter {
  public static final String FORMAT_DATE = "yyyy-MM-dd";

  private final LocationProperties locationProperties;

  public JSONConverter(LocationProperties locationProperties) {
    this.locationProperties = locationProperties;
  }

  public JSONObject toJson(UnformattedWeatherDTO currentWeatherData, boolean extended) {
    JSONObject json = new WeatherJSONObject();
    try {
      json.put("timestamp", currentWeatherData.getTime());
      json.put("temperature", currentWeatherData.getTemperature());
      if (currentWeatherData.getInsideTemperature() != null) {
        json.put("inside_temperature", currentWeatherData.getInsideTemperature());
      }
      json.put("humidity", currentWeatherData.getHumidity());
      if (currentWeatherData.getInsideHumidity() != null) {
        json.put("inside_humidity", currentWeatherData.getInsideHumidity());
      }
      json.put("rain", currentWeatherData.getRainLastHour());
      json.put("rain_today", currentWeatherData.getRainToday());
      json.put("raining", currentWeatherData.isRaining());
      json.put("wind", currentWeatherData.getWindspeed());

      json.put("location", locationProperties.getCity());
      if (extended) {
        json.put("forecast", locationProperties.getWeatherForecast());
      }
    } catch (JSONException ignored) {
    }
    return json;
  }

  public JSONObject toJson(RainDTO rain) {
    JSONObject json = new WeatherJSONObject();
    try {
      json.put("lastHour", rain.getLastHour());
      json.put("today", rain.getToday());
      json.put("yesterday", rain.getYesterday());
      json.put("lastWeek", rain.getLastWeek());
      json.put("last30days", rain.getLast30Days());
    } catch (JSONException ignored) {
    }
    return json;
  }

  private static final String FORMAT_OUTGOING_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";

  public ArrayList<JSONObject> toJson(List<SmoothedWeatherDataSet> list, boolean extended) {
    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_OUTGOING_TIMESTAMP);

    Integer previousRainCounter = null;
    ArrayList<JSONObject> jsonObjects = new ArrayList<>();
    for (SmoothedWeatherDataSet wds : list) {
      JSONObject json = new WeatherJSONObject();
      try {
        json.put("timestamp", sdf.format(DateUtil.fromGMTtoCEST(wds.getTimestamp())));
        json.put("temperature", wds.getOutsideTemperature());
        if (wds.getInsideTemperature() != null && extended) {
          json.put("inside_temperature", wds.getInsideTemperature());
        }

        json.put("humidity", wds.getOutsideHumidity());
        if (wds.getInsideHumidity() != null && extended) {
          json.put("inside_humidity", wds.getInsideHumidity());
        }

        if (previousRainCounter != null && wds.getRainCounter() != null) {
          double rain = 0.295 * (wds.getRainCounter() - previousRainCounter);
          json.put("rain", Math.max(rain, 0));
        } else {
          json.put("rain", 0.0);
        }
        previousRainCounter = wds.getRainCounter();
        json.put("wind", wds.getWindspeed());
        json.put("windMax", wds.getWindspeedMax());
      } catch (JSONException ignored) {
      }
      jsonObjects.add(json);
    }

    return jsonObjects;
  }


  public ArrayList<JSONObject> toJson(List<AggregatedWeatherDataSet> list) {
    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);

    ArrayList<JSONObject> jsonObjects = new ArrayList<>();
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
      } catch (JSONException ignored) {
      }
      jsonObjects.add(json);
    }
    return jsonObjects;
  }

}

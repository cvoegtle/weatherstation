package org.voegtle.weatherstation.server.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.data.RainDTO;
import org.voegtle.weatherstation.server.data.Statistics;
import org.voegtle.weatherstation.server.data.StatisticsSet;
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
  private final DateUtil dateUtil;

  public JSONConverter(LocationProperties locationProperties) {
    this.locationProperties = locationProperties;
    this.dateUtil = locationProperties.getDateUtil();
  }

  public JSONObject toJson(UnformattedWeatherDTO currentWeatherData) {
    JSONObject json = new WeatherJSONObject();
    try {
      json.put("timestamp", currentWeatherData.getTime());
      json.putOpt("temperature", currentWeatherData.getTemperature());
      json.putOpt("inside_temperature", currentWeatherData.getInsideTemperature());
      json.putOpt("humidity", currentWeatherData.getHumidity());
      json.putOpt("inside_humidity", currentWeatherData.getInsideHumidity());
      json.putOpt("rain", currentWeatherData.getRainLastHour());
      json.putOpt("rain_today", currentWeatherData.getRainToday());

      json.putOpt("raining", currentWeatherData.isRaining());
      json.putOpt("wind", multiply(currentWeatherData.getWindspeed(), locationProperties.getWindMultiplier()));
      json.putOpt("watt", currentWeatherData.getWatt());

      json.put("location", locationProperties.getCity());
      json.put("location_short", locationProperties.getCityShortcut());
      json.putOpt("localtime", currentWeatherData.getLocalTime());

      json.put("id", locationProperties.getLocation());
      json.putOpt("forecast", locationProperties.getWeatherForecast());
    } catch (JSONException ignored) {
    }
    return json;
  }

  public JSONObject toJsonLegacy(UnformattedWeatherDTO currentWeatherData, boolean extended) {
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
      json.put("wind", multiply(currentWeatherData.getWindspeed(), locationProperties.getWindMultiplier()));
      if (currentWeatherData.getWatt() != null) {
        json.put("watt", currentWeatherData.getWatt());
      }

      json.put("location", locationProperties.getCity());
      json.put("location_short", locationProperties.getCityShortcut());

      json.put("id", locationProperties.getLocation());
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

  public ArrayList<JSONObject> toJson(List<SmoothedWeatherDataSet> list, boolean extended) {
    Integer previousRainCounter = null;
    ArrayList<JSONObject> jsonObjects = new ArrayList<>();
    for (SmoothedWeatherDataSet wds : list) {
      JSONObject json = new WeatherJSONObject();
      try {
        json.put("timestamp", dateUtil.toLocalDate(wds.getTimestamp()));
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
        json.put("wind", multiply(wds.getWindspeed(), locationProperties.getWindMultiplier()));
        json.put("windMax", multiply(wds.getWindspeedMax(), locationProperties.getWindMultiplier()));

        json.put("watt", wds.getWatt());
      } catch (JSONException ignored) {
      }
      jsonObjects.add(json);
    }

    return jsonObjects;
  }

  private Float multiply(Float number, Float factor) {
    if (number != null) {
      number *= factor;
    }
    return number;
  }


  public ArrayList<JSONObject> toJsonAggregated(List<AggregatedWeatherDataSet> list, boolean extended) {
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
        json.put("wind", multiply(wds.getWindspeedAverage(), locationProperties.getWindMultiplier()));
        json.put("windMax", multiply(wds.getWindspeedMax(), locationProperties.getWindMultiplier()));
        double rain = 0.295 * (wds.getRainCounter());
        json.put("rain", Math.max(rain, 0));
        if (extended) {
          json.put("kwh", wds.getKwh());
        }
      } catch (JSONException ignored) {
      }
      jsonObjects.add(json);
    }
    return jsonObjects;
  }

  public JSONObject toJson(Statistics stats) {
    JSONObject json = new WeatherJSONObject();
    try {
      json.put("id", locationProperties.getLocation());

      ArrayList<JSONObject> jsonObjects = new ArrayList<>();
      if (stats.getRainLastHour() != null) {
        StatisticsSet lastHour = new StatisticsSet();
        lastHour.addRain(stats.getRainLastHour());
        jsonObjects.add(toJson(Statistics.TimeRange.lastHour, lastHour));
      }

      jsonObjects.add(toJson(Statistics.TimeRange.today, stats.getToday()));
      jsonObjects.add(toJson(Statistics.TimeRange.yesterday, stats.getYesterday()));
      jsonObjects.add(toJson(Statistics.TimeRange.last7days, stats.getLast7days()));
      jsonObjects.add(toJson(Statistics.TimeRange.last30days, stats.getLast30days()));
      json.put("stats", jsonObjects);
    } catch (JSONException ignored) {
    }
    return json;
  }

  private JSONObject toJson(Statistics.TimeRange range, StatisticsSet set) throws JSONException {
    JSONObject json = new WeatherJSONObject();
    json.put("range", range);
    json.put("rain", set.getRain());
    json.put("minTemperature", set.getMinTemperature());
    json.put("maxTemperature", set.getMaxTemperature());
    if (set.getKwh() != null) {
      json.put("kwh", set.getKwh());
    }
    return json;
  }
}

package org.voegtle.weatherstation.server.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.data.RainDTO;
import org.voegtle.weatherstation.server.data.Statistics;
import org.voegtle.weatherstation.server.data.StatisticsSet;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JSONConverter {
  private static final String FORMAT_DATE = "yyyy-MM-dd";

  private final LocationProperties locationProperties;

  public JSONConverter(LocationProperties locationProperties) {
    this.locationProperties = locationProperties;
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

      json.putOpt("latitude", locationProperties.getLatitude());
      json.putOpt("longitude", locationProperties.getLongitude());

    } catch (JSONException ignored) {
    }
    return json;
  }

  public JSONObject toJson(CacheWeatherDTO currentWeatherData) {
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
      json.putOpt("wind", currentWeatherData.getWindspeed());
      json.putOpt("watt", currentWeatherData.getWatt());

      json.put("location", currentWeatherData.getLocation());
      json.put("location_short", currentWeatherData.getLocationShort());
      json.putOpt("localtime", currentWeatherData.getLocalTime());

      json.put("id", currentWeatherData.getId());
      json.putOpt("forecast", currentWeatherData.getForecast());

      json.putOpt("latitude", currentWeatherData.getLatitude());
      json.putOpt("longitude", currentWeatherData.getLongitude());

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
    DateUtil dateUtil = locationProperties.getDateUtil();

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

  public JSONObject toJson(Statistics stats, boolean newFormat) {
    JSONObject json = new WeatherJSONObject();
    try {
      json.put("id", locationProperties.getLocation());

      ArrayList<JSONObject> jsonObjects = new ArrayList<>();
      if (stats.getRainLastHour() != null) {
        StatisticsSet lastHour = new StatisticsSet();
        lastHour.addRain(stats.getRainLastHour());
        jsonObjects.add(toJson(Statistics.TimeRange.lastHour, lastHour, newFormat));
      }

      jsonObjects.add(toJson(Statistics.TimeRange.today, stats.getToday(), newFormat));
      jsonObjects.add(toJson(Statistics.TimeRange.yesterday, stats.getYesterday(), newFormat));
      jsonObjects.add(toJson(Statistics.TimeRange.last7days, stats.getLast7days(), newFormat));
      jsonObjects.add(toJson(Statistics.TimeRange.last30days, stats.getLast30days(), newFormat));
      json.put("stats", jsonObjects);
    } catch (JSONException ignored) {
    }
    return json;
  }

  private JSONObject toJson(Statistics.TimeRange range, StatisticsSet set, boolean newFormat) throws JSONException {
    JSONObject json = new WeatherJSONObject();
    json.put("range", range);
    if (newFormat) {
      json.putOpt("rain", set.getRain());
      json.putOpt("minTemperature", set.getMinTemperature());
      json.putOpt("maxTemperature", set.getMaxTemperature());
    } else {
      json.put("rain", set.getRain());
      json.put("minTemperature", set.getMinTemperature());
      json.put("maxTemperature", set.getMaxTemperature());
    }
    json.putOpt("kwh", set.getKwh());
    return json;
  }

  public CacheWeatherDTO decodeWeatherDTO(String encodedWeatherData) throws JSONException {
    CacheWeatherDTO weatherDTO = new CacheWeatherDTO();
    JSONObject json = new JSONObject(encodedWeatherData);
    String timestamp = json.getString("timestamp");
    weatherDTO.setTime(new Date(timestamp));

    weatherDTO.setId(json.getString("id"));
    weatherDTO.setForecast(json.getString("forecast"));
    weatherDTO.setLocation(json.getString("location"));
    weatherDTO.setLocationShort(json.getString("location_short"));

    Number temperature = (Number) json.get("temperature");
    weatherDTO.setTemperature(temperature.floatValue());

    if (json.has("localtime")) {
      weatherDTO.setLocalTime((String)json.get("localtime"));
    }

    if (json.has("inside_temperature")) {
      Number insideTemperature = (Number) json.get("inside_temperature");
      weatherDTO.setInsideTemperature(insideTemperature.floatValue());
    }

    Number humidity = (Number) json.get("humidity");
    weatherDTO.setHumidity(humidity.floatValue());

    if (json.has("inside_humidity")) {
      Number insideHumidity = (Number) json.get("inside_humidity");
      weatherDTO.setInsideHumidity(insideHumidity.floatValue());
    }

    if (json.has("watt")) {
      Number watt = (Number) json.get("watt");
      weatherDTO.setWatt(watt.floatValue());
    }

    if (json.has("rain")) {
      Number rain = (Number)json.get("rain");
      weatherDTO.setRainLastHour(rain.floatValue());
    }

    if (json.has("rain_today")) {
      Number rainToday = (Number)json.get("rain_today");
      weatherDTO.setRainToday(rainToday.floatValue());
    }

    if (json.has("raining")) {
      weatherDTO.setRaining(json.getBoolean("raining"));
    }

    if (json.has("wind")) {
      Number wind = (Number)json.get("wind");
      weatherDTO.setWindspeed(wind.floatValue());
    }

    if (json.has("latitude")) {
      Number latitude = (Number)json.get("latitude");
      weatherDTO.setLatitude(latitude.floatValue());
    }

    if (json.has("longitude")) {
      Number longitude = (Number)json.get("longitude");
      weatherDTO.setLongitude(longitude.floatValue());
    }
    return weatherDTO;
  }
}

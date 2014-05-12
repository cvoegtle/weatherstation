package org.voegtle.weatherstation.server.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.LocationProperties;

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
      } catch (JSONException e) {
      }
      jsonObjects.add(json);
    }
    return jsonObjects;
  }

}

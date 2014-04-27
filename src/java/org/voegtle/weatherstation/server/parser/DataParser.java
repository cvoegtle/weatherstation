package org.voegtle.weatherstation.server.parser;

import java.util.Calendar;
import java.util.Locale;

import org.voegtle.weatherstation.server.persistence.WeatherDataSet;

public class DataParser {

  public static int INDEX_INSIDE_TEMPERATURE = 7;
  public static int INDEX_INSIDE_HUMIDITY = 15;
  public static int INDEX_OUTSIDE_TEMPERATURE = 19;
  public static int INDEX_OUTSIDE_HUMIDITY = 20;
  public static int INDEX_WIND_SPEED = 21;
  public static int INDEX_RAINCOUNTER = 22;
  public static int INDEX_RAINING = 23;

  public DataParser() {

  }

  public WeatherDataSet parse(DataLine data) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.set(Calendar.MILLISECOND, 0);

    WeatherDataSet dataSet = new WeatherDataSet(cal.getTime());
    dataSet.setInsideTemperature(parseFloat(data.get(INDEX_INSIDE_TEMPERATURE)));
    dataSet.setInsideHumidity(parseFloat(data.get(INDEX_INSIDE_HUMIDITY)));

    dataSet.setOutsideTemperature(parseFloat(data.get(INDEX_OUTSIDE_TEMPERATURE)));
    dataSet.setOutsideHumidity(parseFloat(data.get(INDEX_OUTSIDE_HUMIDITY)));

    dataSet.setRainCounter(parseInteger(data.get(INDEX_RAINCOUNTER)));
    dataSet.setRaining(parseBoolean(data.get(INDEX_RAINING)));

    dataSet.setWindspeed(parseFloat(data.get(INDEX_WIND_SPEED)));

    return dataSet;
  }

  private Float parseFloat(String val) {
    if ("".equals(val)) {
      return null;
    }
    return Float.parseFloat(val.replace(',', '.'));
  }

  private Integer parseInteger(String val) {
    if ("".equals(val)) {
      return null;
    }
    return Integer.parseInt(val);
  }

  private Boolean parseBoolean(String val) {
    if ("".equals(val)) {
      return null;
    }
    return "1".equals(val);
  }
}

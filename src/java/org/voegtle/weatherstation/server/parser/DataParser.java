package org.voegtle.weatherstation.server.parser;

import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataParser {
// 1;1;;;;;;;;;;;;;;;;;;15,1;78;0,0;616;0;0

  public static int INDEX_INSIDE_TEMPERATURE = 7;
  public static int INDEX_INSIDE_HUMIDITY = 15;
  public static int INDEX_OUTSIDE_TEMPERATURE = 19;
  public static int INDEX_OUTSIDE_HUMIDITY = 20;
  public static int INDEX_WIND_SPEED = 21;
  public static int INDEX_RAINCOUNTER = 22;
  public static int INDEX_RAINING = 23;
  public static int INDEX_DATE = 25;

  public DataParser() {

  }

  public WeatherDataSet parse(DataLine data) throws ParseException {

    Date timestamp = getTimestamp(data);

    WeatherDataSet dataSet = new WeatherDataSet(timestamp);
    dataSet.setInsideTemperature(parseFloat(data.get(INDEX_INSIDE_TEMPERATURE)));
    dataSet.setInsideHumidity(parseFloat(data.get(INDEX_INSIDE_HUMIDITY)));

    dataSet.setOutsideTemperature(parseFloat(data.get(INDEX_OUTSIDE_TEMPERATURE)));
    dataSet.setOutsideHumidity(parseFloat(data.get(INDEX_OUTSIDE_HUMIDITY)));

    dataSet.setRainCounter(parseInteger(data.get(INDEX_RAINCOUNTER)));
    dataSet.setRaining(parseBoolean(data.get(INDEX_RAINING)));

    dataSet.setWindspeed(parseFloat(data.get(INDEX_WIND_SPEED)));

    return dataSet;
  }

  private Date getTimestamp(DataLine data) throws ParseException {
    String timeString = data.get(INDEX_DATE);
    if (StringUtil.isEmpty(timeString)) {
      Calendar cal = Calendar.getInstance(Locale.GERMANY);
      cal.set(Calendar.MILLISECOND, 0);
      return cal.getTime();
    }
    return parseTimestamp(timeString);
  }

  private static final String FORMAT_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ssZ";
  private static final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIMESTAMP);

  private Date parseTimestamp(String val) throws ParseException {
    return sdf.parse(val);
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

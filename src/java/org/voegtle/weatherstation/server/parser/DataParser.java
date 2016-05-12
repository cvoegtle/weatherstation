package org.voegtle.weatherstation.server.parser;

import org.voegtle.weatherstation.server.persistence.StationTypeEnum;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataParser {
// 1;1;;;;;;;;;;;;;;;;;;15,1;78;0,0;616;0;0

  private static final int INDEX_MICRO_OUTSIDE_TEMPERATURE = 10;
  private static final int INDEX_MICRO_OUTSIDE_HUMIDITY = 18;
  private static final int INDEX_INSIDE_TEMPERATURE = 7;
  private static final int INDEX_INSIDE_HUMIDITY = 15;
  private static final int INDEX_OUTSIDE_TEMPERATURE = 19;
  private static final int INDEX_OUTSIDE_HUMIDITY = 20;
  private static final int INDEX_WIND_SPEED = 21;
  private static final int INDEX_RAINCOUNTER = 22;
  private static final int INDEX_RAINING = 23;
  private static final int INDEX_DATE = 25;
  private static final int INDEX_WATT = 26;
  private static final int INDEX_KWH = 27;

  private static final Date MIN_DATE = DateUtil.getDate(2014, 1, 1);

  private StationTypeEnum stationType;

  public DataParser(StationTypeEnum stationType) {
    this.stationType = stationType;
  }

  public List<WeatherDataSet> parse(List<DataLine> lines) throws ParseException {
    ArrayList<WeatherDataSet> dataSets = new ArrayList<>();

    WeatherDataSet lastDataSet = null;
    for (DataLine line : lines) {
      WeatherDataSet dataSet = parse(line);
      if (dataSet != null) {
        lastDataSet = dataSet;
        dataSets.add(lastDataSet);
      }
    }

    repairDate(lastDataSet);

    return dataSets;
  }

  /**
   * es kann vorkommen, dass das Datum der Fritzbox falsch gesetzt ist. Meist 1970.
   * In diesem Fall zumindest für den letzten Datum die aktuelle Zeit setzen.
   * Denn dieser Datensatz hat die Übertragung ausgelöst.
   */
  private void repairDate(WeatherDataSet dataSet) {

    if (dataSet != null && dataSet.getTimestamp().before(MIN_DATE)) {
      dataSet.setTimestamp(new Date());
    }
  }

  private WeatherDataSet parse(DataLine data) throws ParseException {
    if (isValid(data)) {
      Date timestamp = getTimestamp(data);

      WeatherDataSet dataSet = new WeatherDataSet(timestamp);
      dataSet.setInsideTemperature(parseFloat(data.get(INDEX_INSIDE_TEMPERATURE)));
      dataSet.setInsideHumidity(parseFloat(data.get(INDEX_INSIDE_HUMIDITY)));

      dataSet.setOutsideTemperature(parseFloat(data.get(getOutsideTemperatureIndex())));
      dataSet.setOutsideHumidity(parseFloat(data.get(getOutsideHumidityIndex())));

      dataSet.setRainCounter(parseInteger(data.get(INDEX_RAINCOUNTER)));
      dataSet.setRaining(parseBoolean(data.get(INDEX_RAINING)));

      dataSet.setWindspeed(parseFloat(data.get(INDEX_WIND_SPEED)));
      dataSet.setWatt(parseFloat(data.get(INDEX_WATT)));
      dataSet.setKwh(parseDouble(data.get(INDEX_KWH)));

      return dataSet;
    } else {
      return null;
    }
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

  private boolean isValid(DataLine data) {
      return data.size() > INDEX_DATE && StringUtil.isNotEmpty(data.get(getOutsideTemperatureIndex()));
  }

  private int getOutsideTemperatureIndex() {
    return stationType == StationTypeEnum.STANDARD ? INDEX_OUTSIDE_TEMPERATURE : INDEX_MICRO_OUTSIDE_TEMPERATURE;
  }

  private int getOutsideHumidityIndex() {
    return stationType == StationTypeEnum.STANDARD ? INDEX_OUTSIDE_HUMIDITY: INDEX_MICRO_OUTSIDE_HUMIDITY;
  }

  private static final String FORMAT_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ssZ";
  private static final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIMESTAMP);

  private Date parseTimestamp(String val) throws ParseException {
    return sdf.parse(val);
  }

  private Float parseFloat(String val) {
    if (StringUtil.isEmpty(val)) {
      return null;
    }
    return Float.parseFloat(val.replace(',', '.'));
  }

  private Double parseDouble(String val) {
    if (StringUtil.isEmpty(val)) {
      return null;
    }
    return Double.parseDouble(val.replace(',', '.'));
  }

  private Integer parseInteger(String val) {
    if (StringUtil.isEmpty(val)) {
      return null;
    }
    return Integer.parseInt(val);
  }

  private Boolean parseBoolean(String val) {
    if (StringUtil.isEmpty(val)) {
      return null;
    }
    return "1".equals(val);
  }
}

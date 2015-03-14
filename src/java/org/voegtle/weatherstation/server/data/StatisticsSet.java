package org.voegtle.weatherstation.server.data;

public class StatisticsSet {
  Float rain;
  Float maxTemperature;
  Float minTemperature;

  public StatisticsSet() {
  }

  public void addRain(Float rain) {
    if (rain == null) {
      return;
    }

    if (this.rain == null) {
      this.rain = rain;
    } else {
      this.rain += rain;
    }
  }

  public Float getRain() {
    return rain;
  }

  public void setTemperature(Float temperature) {
    if (temperature == null) {
      return;
    }

    if (maxTemperature == null || maxTemperature.compareTo(temperature) < 0) {
      maxTemperature = temperature;
    }
    if (minTemperature == null || minTemperature.compareTo(temperature) > 0) {
      minTemperature = temperature;
    }
  }

  public Float getMinTemperature() {
    return minTemperature;
  }

  public Float getMaxTemperature() {
    return maxTemperature;
  }

}

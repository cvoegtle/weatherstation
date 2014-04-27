package org.voegtle.weatherstation.client.dto;

import java.io.Serializable;

public class WeatherDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private String time;
  private String temperature;
  private String humidity;
  private String rainLastHour;
  private Boolean raining;

  private String windspeed;

  private String insideTemperature;
  private String insideHumidity;

  public WeatherDTO() {
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getTemperature() {
    return temperature;
  }

  public void setTemperature(String temperature) {
    this.temperature = temperature;
  }

  public String getHumidity() {
    return humidity;
  }

  public void setHumidity(String humidity) {
    this.humidity = humidity;
  }

  public Boolean getRaining() {
    return raining;
  }

  public void setRaining(Boolean raining) {
    this.raining = raining;
  }

  public String getRainLastHour() {
    return rainLastHour;
  }

  public void setRainLastHour(String rainLastHour) {
    this.rainLastHour = rainLastHour;
  }

  public String getInsideTemperature() {
    return insideTemperature;
  }

  public void setInsideTemperature(String insideTemperature) {
    this.insideTemperature = insideTemperature;
  }

  public String getInsideHumidity() {
    return insideHumidity;
  }

  public void setInsideHumidity(String insideHumidity) {
    this.insideHumidity = insideHumidity;
  }

  public String getWindspeed() {
    return windspeed;
  }

  public void setWindspeed(String windspeed) {
    this.windspeed = windspeed;
  }

}

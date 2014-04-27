package org.voegtle.weatherstation.client.dto;

import java.io.Serializable;
import java.util.Date;

public class UnformattedWeatherDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private Date time;
  Float temperature;
  Float humidity;
  boolean raining;
  Float rainLastHour;
  Float rainToday;
  Float windspeed;

  public UnformattedWeatherDTO() {
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public Float getTemperature() {
    return temperature;
  }

  public void setTemperature(Float temperature) {
    this.temperature = temperature;
  }

  public Float getHumidity() {
    return humidity;
  }

  public void setHumidity(Float humidity) {
    this.humidity = humidity;
  }

  public boolean isRaining() {
    return raining;
  }

  public void setRaining(boolean raining) {
    this.raining = raining;
  }

  public Float getRainLastHour() {
    return rainLastHour;
  }

  public void setRainLastHour(Float rainLastHour) {
    this.rainLastHour = rainLastHour;
  }

  public Float getWindspeed() {
    return windspeed;
  }

  public void setWindspeed(Float windspeed) {
    this.windspeed = windspeed;
  }

  public Float getRainToday() {
    return rainToday;
  }

  public void setRainToday(Float rainToday) {
    this.rainToday = rainToday;
  }

}

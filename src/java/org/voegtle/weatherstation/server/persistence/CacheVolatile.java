package org.voegtle.weatherstation.server.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class CacheVolatile {
  @Id
  private String id;
  private Date time;
  private String localTime;

  private Float temperature;
  private Float insideTemperature;
  private Float humidity;
  private Boolean raining;
  private Float rainLastHour;
  private Float rainToday;
  private Float windspeed;
  private Float insideHumidity;
  private Float watt;

  public void copyFrom(CacheWeatherDTO dto) {
    this.id = dto.getId();
    // copy only values that might change
    this.time = dto.getTime();
    this.localTime = dto.getLocalTime();

    this.temperature = dto.getTemperature();

    this.insideTemperature = dto.getInsideTemperature();
    this.humidity = dto.getHumidity();
    this.insideHumidity = dto.getInsideHumidity();

    this.raining = dto.getRaining();

    this.rainLastHour = dto.getRainLastHour();
    this.rainToday = dto.getRainToday();
    this.windspeed = dto.getWindspeed();
    this.watt = dto.getWatt();
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getLocalTime() {
    return localTime;
  }

  public void setLocalTime(String localTime) {
    this.localTime = localTime;
  }

  public Float getTemperature() {
    return temperature;
  }

  public void setTemperature(Float temperature) {
    this.temperature = temperature;
  }

  public Float getInsideTemperature() {
    return insideTemperature;
  }

  public void setInsideTemperature(Float insideTemperature) {
    this.insideTemperature = insideTemperature;
  }

  public Float getHumidity() {
    return humidity;
  }

  public void setHumidity(Float humidity) {
    this.humidity = humidity;
  }

  public Boolean getRaining() {
    return raining;
  }

  public void setRaining(Boolean raining) {
    this.raining = raining;
  }

  public Float getRainLastHour() {
    return rainLastHour;
  }

  public void setRainLastHour(Float rainLastHour) {
    this.rainLastHour = rainLastHour;
  }

  public Float getRainToday() {
    return rainToday;
  }

  public void setRainToday(Float rainToday) {
    this.rainToday = rainToday;
  }

  public Float getWindspeed() {
    return windspeed;
  }

  public void setWindspeed(Float windspeed) {
    this.windspeed = windspeed;
  }

  public Float getInsideHumidity() {
    return insideHumidity;
  }

  public void setInsideHumidity(Float insideHumidity) {
    this.insideHumidity = insideHumidity;
  }

  public Float getWatt() {
    return watt;
  }

  public void setWatt(Float watt) {
    this.watt = watt;
  }
}

package org.voegtle.weatherstation.server.persistence;

import java.io.Serializable;
import java.util.Date;

public class CacheWeatherDTO implements Serializable {

  private String id;
  private Date time;
  private String localTime;
  private String location;
  private String locationShort;
  private Float temperature;
  private Float insideTemperature;
  private Float humidity;
  private Float insideHumidity;
  private Boolean raining;
  private Float rainLastHour;
  private Float rainToday;
  private Float windspeed;
  private Float watt;
  private String forecast;

  public CacheWeatherDTO() {
  }

  public CacheWeatherDTO(CacheStatic cacheStatic, CacheVolatile cacheVolatile) {
    this.id = cacheVolatile.getId();
    this.time = cacheVolatile.getTime();
    this.localTime = cacheVolatile.getLocalTime();
    this.location = cacheStatic.getLocation();
    this.locationShort = cacheStatic.getLocationShort();
    this.temperature = cacheVolatile.getTemperature();
    this.insideTemperature = cacheVolatile.getInsideTemperature();
    this.humidity = cacheVolatile.getHumidity();
    this.insideHumidity = cacheVolatile.getInsideHumidity();
    this.raining = cacheVolatile.getRaining();
    this.rainLastHour = cacheVolatile.getRainLastHour();
    this.rainToday = cacheVolatile.getRainToday();
    this.windspeed = cacheVolatile.getWindspeed();
    this.watt = cacheVolatile.getWatt();
    this.forecast = cacheStatic.getForecast();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Boolean getRaining() {
    return raining;
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

  public Float getHumidity() {
    return humidity;
  }

  public void setHumidity(Float humidity) {
    this.humidity = humidity;
  }

  public Boolean isRaining() {
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

  public Float getInsideTemperature() {
    return insideTemperature;
  }

  public void setInsideTemperature(Float insideTemperature) {
    this.insideTemperature = insideTemperature;
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
  public String getForecast() {
    return forecast;
  }

  public void setForecast(String forecast) {
    this.forecast = forecast;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocationShort() {
    return locationShort;
  }

  public void setLocationShort(String locationShort) {
    this.locationShort = locationShort;
  }
}

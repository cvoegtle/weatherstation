package org.voegtle.weatherstation.server.persistence;

import org.voegtle.weatherstation.server.util.MathUtil;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class CacheWeatherDTO implements Serializable {

  @Id
  private String id;
  private Date time;
  private String localTime;
  private String location;
  private String locationShort;
  private Float temperature;
  private Float insideTemperature;
  private Float humidity;
  private Boolean raining;
  private Float rainLastHour;
  private Float rainToday;
  private Float windspeed;
  private Float insideHumidity;
  private Float watt;
  private String forecast;

  public void copyFrom(CacheWeatherDTO dto) {

    // copy only values that might change
    this.time = dto.time;
    if (StringUtil.compare(this.localTime, dto.localTime) != 0) {
      this.localTime = dto.localTime;
    }

    if (!MathUtil.moreOrLessEqual(this.temperature, dto.temperature)) {
      this.temperature = dto.temperature;
    }

    if (!MathUtil.moreOrLessEqual(this.insideTemperature, dto.insideTemperature)) {
      this.insideTemperature = dto.insideTemperature;
    }
    if (!MathUtil.moreOrLessEqual(this.humidity, dto.humidity)) {
      this.humidity = dto.humidity;
    }
    if (!MathUtil.moreOrLessEqual(this.insideHumidity, dto.insideHumidity)) {
      this.insideHumidity = dto.insideHumidity;
    }

    if (this.raining != dto.raining) {
      this.raining = dto.raining;
    }

    if (!MathUtil.moreOrLessEqual(this.rainLastHour, dto.rainLastHour)) {
      this.rainLastHour = dto.rainLastHour;
    }
    if (!MathUtil.moreOrLessEqual(this.rainToday, dto.rainToday)) {
      this.rainToday = dto.rainToday;
    }
    if (!MathUtil.moreOrLessEqual(this.windspeed, dto.windspeed)) {
      this.windspeed = dto.windspeed;
    }
    if (!MathUtil.moreOrLessEqual(this.watt, dto.watt)) {
      this.watt = dto.watt;
    }
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

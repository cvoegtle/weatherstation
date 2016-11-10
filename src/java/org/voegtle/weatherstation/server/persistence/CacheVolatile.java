package org.voegtle.weatherstation.server.persistence;

import org.voegtle.weatherstation.server.util.MathUtil;
import org.voegtle.weatherstation.server.util.StringUtil;

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
    if (StringUtil.isEmpty(this.id)) {
      this.id = dto.getId();
    }

    this.time = dto.getTime();
    if (StringUtil.compare(this.localTime, dto.getLocalTime()) != 0) {
      this.localTime = dto.getLocalTime();
    }

    if (!MathUtil.moreOrLessEqual(this.temperature, dto.getTemperature())) {
      this.temperature = dto.getTemperature();
    }

    if (!MathUtil.moreOrLessEqual(this.insideTemperature, dto.getInsideTemperature())) {
      this.insideTemperature = dto.getInsideTemperature();
    }
    if (!MathUtil.moreOrLessEqual(this.humidity, dto.getHumidity())) {
      this.humidity = dto.getHumidity();
    }
    if (!MathUtil.moreOrLessEqual(this.insideHumidity, dto.getInsideHumidity())) {
      this.insideHumidity = dto.getInsideHumidity();
    }

    if (this.raining != dto.getRaining()) {
      this.raining = dto.getRaining();
    }

    if (!MathUtil.moreOrLessEqual(this.rainLastHour, dto.getRainLastHour())) {
      this.rainLastHour = dto.getRainLastHour();
    }
    if (!MathUtil.moreOrLessEqual(this.rainToday, dto.getRainToday())) {
      this.rainToday = dto.getRainToday();
    }
    if (!MathUtil.moreOrLessEqual(this.windspeed, dto.getWindspeed())) {
      this.windspeed = dto.getWindspeed();
    }
    if (!MathUtil.moreOrLessEqual(this.watt, dto.getWatt())) {
      this.watt = dto.getWatt();
    }
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

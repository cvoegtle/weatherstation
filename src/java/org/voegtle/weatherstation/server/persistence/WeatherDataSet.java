package org.voegtle.weatherstation.server.persistence;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class WeatherDataSet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private Date timestamp;
  private Float outsideTemperature;
  private Float outsideHumidity;
  private Float insideTemperature;
  private Float insideHumidity;
  private Integer rainCounter;
  private Boolean raining;

  private Float windspeed;

  public WeatherDataSet() {
  }

  public WeatherDataSet(Date timestamp) {
    this.timestamp = timestamp;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public Float getOutsideTemperature() {
    return outsideTemperature;
  }

  public void setOutsideTemperature(Float outsideTemperature) {
    this.outsideTemperature = outsideTemperature;
  }

  public Float getInsideTemperature() {
    return insideTemperature;
  }

  public void setInsideTemperature(Float insideTemperature) {
    this.insideTemperature = insideTemperature;
  }

  public Integer getRainCounter() {
    return rainCounter;
  }

  public void setRainCounter(Integer rainCounter) {
    this.rainCounter = rainCounter;
  }

  public Key getKey() {
    return key;
  }

  public Float getOutsideHumidity() {
    return outsideHumidity;
  }

  public void setOutsideHumidity(Float outsideHumidity) {
    this.outsideHumidity = outsideHumidity;
  }

  public Float getInsideHumidity() {
    return insideHumidity;
  }

  public void setInsideHumidity(Float insideHumidity) {
    this.insideHumidity = insideHumidity;
  }

  public Boolean isRaining() {
    return raining;
  }

  public void setRaining(Boolean raining) {
    this.raining = raining;
  }

  public boolean isValid() {
    return outsideTemperature != null && outsideHumidity != null && rainCounter != null && raining != null;
  }

  public void setWindspeed(Float windspeed) {
    this.windspeed = windspeed;
  }

  public Float getWindSpeed() {
    return windspeed;
  }
}

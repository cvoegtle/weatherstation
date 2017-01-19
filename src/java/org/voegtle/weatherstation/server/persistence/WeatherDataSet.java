package org.voegtle.weatherstation.server.persistence;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

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
  private Float watt;
  private Double kwh;

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
    return outsideTemperature != null && outsideHumidity != null;
  }

  public void setWindspeed(Float windspeed) {
    this.windspeed = windspeed;
  }

  public Float getWindspeed() {
    return windspeed;
  }

  public Float getWatt() {
    return watt;
  }

  public void setWatt(Float watt) {
    this.watt = watt;
  }

  public Double getKwh() {
    return kwh;
  }

  public void setKwh(Double kwh) {
    this.kwh = kwh;
  }

  @Override
  public String toString() {
    return "WeatherDataSet={ TS=" + timestamp + ", OutT=" +outsideTemperature + ", OutH=" + outsideHumidity + ", valid=" +  isValid() + "}" ;
  }

  public static boolean hasRainCounter(WeatherDataSet wds) {
    return wds != null && wds.getRainCounter() != null;
  }
}

package org.voegtle.weatherstation.server.persistence;

import com.google.appengine.api.datastore.Key;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SmoothedWeatherDataSet {
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
  private Float windspeedMax;
  private Float watt;

  @Transient
  private int countOutsideTemperature = 0;
  @Transient
  private int countOutsideHumidity = 0;
  @Transient
  private int countInsideTemperature = 0;
  @Transient
  private int countInsideHumidity = 0;
  @Transient
  private int countWindspeed = 0;
  @Transient
  private int countWatt = 0;

  public SmoothedWeatherDataSet() {
    timestamp = new Date();
  }

  public SmoothedWeatherDataSet(Date timestamp) {
    this.timestamp = timestamp;
  }

  public void add(WeatherDataSet wds) {
    addOutsideTemperature(wds.getOutsideTemperature());
    addOutsideHumidity(wds.getOutsideHumidity());
    addInsideTemperature(wds.getInsideTemperature());
    addInsideHumidity(wds.getInsideHumidity());
    addRainCount(wds.getRainCounter());
    addRaining(wds.isRaining());
    addWindspeed(wds.getWindspeed());
    setWindspeedMaxIfMax(wds.getWindspeed());
    addWatt(wds.getWatt());
  }

  public void normalize() {
    if (countOutsideTemperature > 1) {
      setOutsideTemperature(getOutsideTemperature() / countOutsideTemperature);
    }
    if (countOutsideHumidity > 1) {
      setOutsideHumidity(getOutsideHumidity() / countOutsideHumidity);
    }
    if (countInsideTemperature > 1) {
      setInsideTemperature(getInsideTemperature() / countInsideTemperature);
    }
    if (countInsideHumidity > 1) {
      setInsideHumidity(getInsideHumidity() / countInsideHumidity);
    }
    if (countWindspeed > 1) {
      setWindspeed(getWindspeed() / countWindspeed);
    }
    if (countWatt > 0) {
      setWatt(getWatt() / countWatt);
    }
  }

  private void addOutsideTemperature(Float value) {
    if (value != null) {
      countOutsideTemperature++;
      if (getOutsideTemperature() != null) {
        value = value + getOutsideTemperature();
      }
      setOutsideTemperature(value);
    }
  }

  private void addOutsideHumidity(Float value) {
    if (value != null) {
      countOutsideHumidity++;
      if (getOutsideHumidity() != null) {
        value = value + getOutsideHumidity();
      }
      setOutsideHumidity(value);
    }
  }

  private void addInsideTemperature(Float value) {
    if (value != null) {
      countInsideTemperature++;
      if (getInsideTemperature() != null) {
        value = value + getInsideTemperature();
      }
      setInsideTemperature(value);
    }
  }

  private void addInsideHumidity(Float value) {
    if (value != null) {
      countInsideHumidity++;
      if (getInsideHumidity() != null) {
        value = value + getInsideHumidity();
      }
      setInsideHumidity(value);
    }
  }

  private void addRainCount(Integer rainCounter) {
    if (rainCounter != null) {
      if (getRainCounter() == null) {
        setRainCounter(rainCounter);
      } else if (rainCounter > getRainCounter()) {
        setRainCounter(rainCounter);
      }
    }
  }

  private void addRaining(Boolean raining) {
    if (raining != null) {
      if (isRaining() == null) {
        setRaining(raining);
      } else if (raining) {
        setRaining(true);
      }
    }
  }

  private void addWindspeed(Float value) {
    if (value != null) {
      countWindspeed++;
      if (getWindspeed() != null) {
        value = value + getWindspeed();
      }
      setWindspeed(value);
    }
  }

  private void setWindspeedMaxIfMax(Float value) {
    if (value != null) {
      if (getWindspeedMax() == null || getWindspeedMax().compareTo(value) < 0) {
        setWindspeedMax(value);
      }
    }
  }
  
  private void addWatt(Float value) {
    if (value != null) {
      countWatt++;
      if (getWatt() != null) {
        value = value + getWatt();
      }
      setWatt(value);
    }
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

  public Float getWindspeed() {
    return windspeed;
  }

  public void setWindspeed(Float windspeed) {
    this.windspeed = windspeed;
  }

  public Float getWindspeedMax() {
    return windspeedMax;
  }

  public void setWindspeedMax(Float windspeedMax) {
    this.windspeedMax = windspeedMax;
  }

  public Float getWatt() {
    return watt;
  }

  public void setWatt(Float watt) {
    this.watt = watt;
  }
}

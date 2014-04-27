package org.voegtle.weatherstation.server.persistence;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.appengine.api.datastore.Key;

@Entity
public class AggregatedWeatherDataSet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private Date date;

  @Enumerated(EnumType.STRING)
  private PeriodEnum period;
  private boolean finished;

  private Date timeOfMinimum;
  private Date timeOfMaximum;

  private Float outsideTemperatureMin;
  private Float outsideTemperatureMax;
  private Float outsideTemperatureAverage;
  @Transient
  private int outsideTemperatureCounter;

  private Float outsideHumidityMin;
  private Float outsideHumidityMax;
  private Float outsideHumidityAverage;
  @Transient
  private int outsideHumidityCounter;

  private Float insideTemperatureMin;
  private Float insideTemperatureMax;
  private Float insideTemperatureAverage;
  @Transient
  private int insideTemperatureCounter;

  private Float insideHumidityMin;
  private Float insideHumidityMax;
  private Float insideHumidityAverage;
  @Transient
  private int insideHumidityCounter;

  private Float windspeedMin;
  private Float windspeedMax;
  private Float windspeedAverage;
  @Transient
  private int windspeedCounter;

  private int rainCounter;
  private int rainDays;

  public AggregatedWeatherDataSet(Date date, PeriodEnum period) {
    this.date = date;
    this.period = period;
  }

  public void addOutsideTemperature(Float value, Date time) {
    if (value == null) {
      return;
    }
    if (outsideTemperatureAverage == null) {
      outsideTemperatureAverage = (float) 0.0;
    }

    outsideTemperatureCounter++;
    outsideTemperatureAverage += value;
    if (outsideTemperatureMin == null || outsideTemperatureMin > value) {
      outsideTemperatureMin = value;
      timeOfMinimum = time;
    }
    if (outsideTemperatureMax == null || outsideTemperatureMax < value) {
      outsideTemperatureMax = value;
      timeOfMaximum = time;
    }
  }

  public void addOutsideHumidity(Float value) {
    if (value == null) {
      return;
    }
    if (outsideHumidityAverage == null) {
      outsideHumidityAverage = (float) 0.0;
    }

    outsideHumidityCounter++;
    outsideHumidityAverage += value;
    if (outsideHumidityMin == null || outsideHumidityMin > value) {
      outsideHumidityMin = value;
    }
    if (outsideHumidityMax == null || outsideHumidityMax < value) {
      outsideHumidityMax = value;
    }
  }

  public void addInsideTemperature(Float value) {
    if (value == null) {
      return;
    }
    if (insideTemperatureAverage == null) {
      insideTemperatureAverage = (float) 0.0;
    }

    insideTemperatureCounter++;
    insideTemperatureAverage += value;
    if (insideTemperatureMin == null || insideTemperatureMin > value) {
      insideTemperatureMin = value;
    }
    if (insideTemperatureMax == null || insideTemperatureMax < value) {
      insideTemperatureMax = value;
    }
  }

  public void addInsideHumidity(Float value) {
    if (value == null) {
      return;
    }
    if (insideHumidityAverage == null) {
      insideHumidityAverage = (float) 0.0;
    }

    insideHumidityCounter++;
    insideHumidityAverage += value;
    if (insideHumidityMin == null || insideHumidityMin > value) {
      insideHumidityMin = value;
    }
    if (insideHumidityMax == null || insideHumidityMax < value) {
      insideHumidityMax = value;
    }
  }

  public void addWindspeed(Float value) {
    if (value == null) {
      return;
    }
    if (windspeedAverage == null) {
      windspeedAverage = (float) 0.0;
    }

    windspeedCounter++;
    windspeedAverage += value;
    if (windspeedMin == null || windspeedMin > value) {
      windspeedMin = value;
    }
    if (windspeedMax == null || windspeedMax < value) {
      windspeedMax = value;
    }
  }

  public void normalize() {
    if (outsideTemperatureCounter > 0) {
      outsideTemperatureAverage = outsideTemperatureAverage / outsideTemperatureCounter;
    }
    if (outsideHumidityCounter > 0) {
      outsideHumidityAverage = outsideHumidityAverage / outsideHumidityCounter;
    }

    if (insideTemperatureCounter > 0) {
      insideTemperatureAverage = insideTemperatureAverage / insideTemperatureCounter;
    }
    if (insideHumidityCounter > 0) {
      insideHumidityAverage = insideHumidityAverage / insideHumidityCounter;
    }
    if (windspeedCounter > 0) {
      windspeedAverage = windspeedAverage / windspeedCounter;
    }
  }

  public PeriodEnum getPeriod() {
    return period;
  }

  public void setPeriod(PeriodEnum period) {
    this.period = period;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public int getRainCounter() {
    return rainCounter;
  }

  public void setRainCounter(int rainCounter) {
    this.rainCounter = rainCounter;
  }

  public int getRainDays() {
    return rainDays;
  }

  public void setRainDays(int rainDays) {
    this.rainDays = rainDays;
  }

  public Date getDate() {
    return date;
  }

  public Date getTimeOfMinimum() {
    return timeOfMinimum;
  }

  public Date getTimeOfMaximum() {
    return timeOfMaximum;
  }

  public Float getOutsideTemperatureMin() {
    return outsideTemperatureMin;
  }

  public Float getOutsideTemperatureMax() {
    return outsideTemperatureMax;
  }

  public Float getOutsideTemperatureAverage() {
    return outsideTemperatureAverage;
  }

  public Float getOutsideHumidityMin() {
    return outsideHumidityMin;
  }

  public Float getOutsideHumidityMax() {
    return outsideHumidityMax;
  }

  public Float getOutsideHumidityAverage() {
    return outsideHumidityAverage;
  }

  public Float getInsideTemperatureMin() {
    return insideTemperatureMin;
  }

  public Float getInsideTemperatureMax() {
    return insideTemperatureMax;
  }

  public Float getInsideTemperatureAverage() {
    return insideTemperatureAverage;
  }

  public Float getInsideHumidityMin() {
    return insideHumidityMin;
  }

  public Float getInsideHumidityMax() {
    return insideHumidityMax;
  }

  public Float getInsideHumidityAverage() {
    return insideHumidityAverage;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public Float getWindspeedMin() {
    return windspeedMin;
  }

  public void setWindspeedMin(Float windspeedMin) {
    this.windspeedMin = windspeedMin;
  }

  public Float getWindspeedMax() {
    return windspeedMax;
  }

  public void setWindspeedMax(Float windspeedMax) {
    this.windspeedMax = windspeedMax;
  }

  public Float getWindspeedAverage() {
    return windspeedAverage;
  }

  public void setWindspeedAverage(Float windspeedAverage) {
    this.windspeedAverage = windspeedAverage;
  }

}

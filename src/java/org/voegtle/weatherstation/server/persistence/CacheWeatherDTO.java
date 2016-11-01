package org.voegtle.weatherstation.server.persistence;

import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CacheWeatherDTO extends UnformattedWeatherDTO {

  @Id
  private String id;

  private String forecast;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getForecast() {
    return forecast;
  }

  public void setForecast(String forecast) {
    this.forecast = forecast;
  }
}

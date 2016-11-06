package org.voegtle.weatherstation.server.persistence;


import org.voegtle.weatherstation.server.util.StringUtil;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CacheStatic {

  @Id
  private String id;
  private String location;
  private String locationShort;
  private String forecast;

  public boolean copyFrom(CacheWeatherDTO dto) {
    boolean changed = false;
    this.id = dto.getId();
    if (StringUtil.compare(this.location, dto.getLocation()) != 0) {
      this.location = dto.getLocation();
      changed = true;
    }
    if (StringUtil.compare(this.locationShort, dto.getLocationShort()) != 0) {
      this.locationShort = dto.getLocationShort();
      changed = true;
    }
    if (StringUtil.compare(this.forecast, dto.getForecast()) != 0) {
      this.forecast = dto.getForecast();
      changed = true;
    }
    return changed;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getForecast() {
    return forecast;
  }

  public void setForecast(String forecast) {
    this.forecast = forecast;
  }
}

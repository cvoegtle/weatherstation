package org.voegtle.weatherstation.server.persistence;

import com.google.appengine.api.datastore.Key;
import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.TimeZone;

@Entity
public class LocationProperties {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private String location;
  private String city;
  private String cityShortcut;
  private String timezone;

  private String address;

  private String secretHash;

  private String readHash;

  private String weatherForecast;

  private Boolean windRelevant;

  private Float windMultiplier;

  private StationTypeEnum stationType;

  public LocationProperties() {
  }

  public boolean isValid() {
    return StringUtil.isNotEmpty(location) && StringUtil.isNotEmpty(city) && StringUtil.isNotEmpty(address);
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String id) {
    this.location = id;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCityShortcut() {
    return cityShortcut;
  }

  public void setCityShortcut(String cityShortcut) {
    this.cityShortcut = cityShortcut;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSecretHash() {
    return secretHash;
  }

  public void setSecretHash(String secretHash) {
    this.secretHash = secretHash;
  }

  public String getWeatherForecast() {
    return weatherForecast;
  }

  public void setWeatherForecast(String weatherForecast) {
    this.weatherForecast = weatherForecast;
  }


  public String getReadHash() {
    return readHash;
  }

  public void setReadHash(String readHash) {
    this.readHash = readHash;
  }

  public boolean isWindRelevant() {
    return windRelevant != null && windRelevant;
  }

  public void setWindRelevant(Boolean windRelevant) {
    this.windRelevant = windRelevant;
  }

  public Float getWindMultiplier() {
    if (windMultiplier == null) {
      windMultiplier = 1.0f;
    }
    return windMultiplier;
  }

  public void setWindMultiplier(Float windMultiplier) {
    this.windMultiplier = windMultiplier;
  }

  public StationTypeEnum getStationType() {
    if (stationType == null) {
      stationType = StationTypeEnum.STANDARD;
    }
    return stationType;
  }

  public void setStationType(StationTypeEnum stationType) {
    this.stationType = stationType;
  }

  public String getTimezone() {
    return timezone;
  }

  public DateUtil getDateUtil() {
    return new DateUtil(TimeZone.getTimeZone(timezone));
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
}

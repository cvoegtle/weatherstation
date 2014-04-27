package org.voegtle.weatherstation.server.persistence;

import org.voegtle.weatherstation.client.util.StringUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.google.appengine.api.datastore.Key;

@Entity
public class LocationProperties {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private String location;
  private String city;

  private String address;

  private String secretHash;

  private String weatherForecast;

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


}

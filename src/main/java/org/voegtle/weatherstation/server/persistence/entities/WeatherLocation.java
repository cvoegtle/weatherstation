package org.voegtle.weatherstation.server.persistence.entities;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WeatherLocation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private String location;
  private String host;
  private boolean forwardSecret;
  private String readHash;

  public WeatherLocation() {
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public boolean isForwardSecret() {
    return forwardSecret;
  }

  public void setForwardSecret(boolean forwardSecret) {
    this.forwardSecret = forwardSecret;
  }

  public String getReadHash() {
    return readHash;
  }

  public void setReadHash(String readHash) {
    this.readHash = readHash;
  }
  
}

package org.voegtle.weatherstation.server.persistence.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class WeatherLocation {
  @Id Long id;

  private String location;
  private String host;
  private boolean forwardSecret;
  private String readHash;

  public WeatherLocation() {
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

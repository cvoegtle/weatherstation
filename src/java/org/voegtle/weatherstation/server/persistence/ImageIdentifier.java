package org.voegtle.weatherstation.server.persistence;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ImageIdentifier {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private String oid;
  private String zx;

  public ImageIdentifier() {
  }

  public ImageIdentifier(String oid, String zx) {
    this.oid = oid;
    this.zx = zx;
  }

  public String asUrlParameter() {
    return "&oid=" + oid + "&zx=" + zx;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public void setZx(String zx) {
    this.zx = zx;
  }

  public String getOid() {
    return oid;
  }

  public String getZx() {
    return zx;
  }
}

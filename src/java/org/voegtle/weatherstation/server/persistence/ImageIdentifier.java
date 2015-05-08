package org.voegtle.weatherstation.server.persistence;

import com.google.appengine.api.datastore.Key;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ImageIdentifier {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private Integer sheet;
  private String oid;
  private String zx;
  private String format;

  public ImageIdentifier() {
  }

  public ImageIdentifier(String oid, String zx) {
    this.sheet = 0;
    this.oid = oid;
    this.zx = zx;
  }

  public ImageIdentifier(Integer sheet, String oid, String format) {
    this.sheet = sheet;
    this.oid = oid;
    this.format = format;
  }

  public String asUrlParameter() {
    if (StringUtil.isEmpty(zx)) {
      return "?oid=" + oid + "&format=" + format;
    } else {
      return "&oid=" + oid + "&zx=" + zx;
    }
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

  public Integer getSheet() {
    return sheet;
  }

  public void setSheet(Integer sheet) {
    this.sheet = sheet;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }
}

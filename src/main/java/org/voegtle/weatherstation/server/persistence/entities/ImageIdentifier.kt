package org.voegtle.weatherstation.server.persistence.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import org.voegtle.weatherstation.server.util.StringUtil;


@Entity
public class ImageIdentifier {
  @Id Long id;

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
    if (StringUtil.INSTANCE.isEmpty(zx)) {
      return "?oid=" + oid + "&format=" + format;
    } else {
      return "&oid=" + oid + "&zx=" + zx;
    }
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

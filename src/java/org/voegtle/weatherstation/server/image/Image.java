package org.voegtle.weatherstation.server.image;

import java.util.Calendar;
import java.util.Date;

public class Image {
  private String oid;
  private Date creation;
  private byte[] png;

  public Image(String oid, Date creation, byte[] png) {
    this.oid = oid;
    this.png = png;
    this.creation = creation;
  }

  public String getOid() {
    return oid;
  }

  public byte[] getPng() {
    return png;
  }

  public boolean isOld() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -1);
    return cal.after(creation);
  }
}

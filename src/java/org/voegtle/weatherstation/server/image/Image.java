package org.voegtle.weatherstation.server.image;

import com.google.appengine.api.datastore.Blob;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;

@Entity
public class Image {
  @Id
  private String oid;
  private Date creation;
  @Basic
  private Blob png;

  public Image() {
  }

  public Image(String oid, Date creation, byte[] png) {
    this.oid = oid;
    this.png = new Blob(png);
    this.creation = creation;
  }

  public String getOid() {
    return oid;
  }

  public byte[] getPngAsBytes() {
    return png.getBytes();
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Date getCreation() {
    return creation;
  }

  public void setCreation(Date creation) {
    this.creation = creation;
  }

  public void setPng(Blob png) {
    this.png = png;
  }

  public Blob getPng() {
    return png;
  }

  public boolean isOld() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -1);
    return cal.after(creation);
  }
}

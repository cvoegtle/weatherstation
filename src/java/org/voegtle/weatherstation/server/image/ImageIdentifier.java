package org.voegtle.weatherstation.server.image;

public class ImageIdentifier {
  private String oid;
  private String zx;

  public ImageIdentifier(String oid, String zx) {
    this.oid = oid;
    this.zx = zx;
  }

  public String asUrlParameter() {
    return "&oid=" + oid + "&zx=" + zx;
  }

  public String getOid() {
    return oid;
  }

  public String getZx() {
    return zx;
  }
}

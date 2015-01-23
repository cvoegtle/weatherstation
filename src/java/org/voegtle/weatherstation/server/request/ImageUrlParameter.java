package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;

public class ImageUrlParameter extends AbstractUrlParameter {
  private static final String PARAM_OID = "oid";
  private static final String PARAM_ZX = "zx";
  private static final String PARAM_REFRESH = "refresh";

  private String oid;
  private String zx;
  private boolean refresh;

  public ImageUrlParameter(HttpServletRequest request) {
    super(request);
    oid = getUrlParameter(PARAM_OID);
    zx = getUrlParameter(PARAM_ZX);
    refresh = getUrlParameterBoolean(PARAM_REFRESH);
  }

  public String getOid() {
    return oid;
  }

  public String getZx() {
    return zx;
  }

  public boolean isRefresh() {
    return refresh;
  }
}

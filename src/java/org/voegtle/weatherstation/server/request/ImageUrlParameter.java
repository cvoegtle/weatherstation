package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;

public class ImageUrlParameter extends AbstractUrlParameter {
  private static final String PARAM_OID = "oid";
  private static final String PARAM_ZX = "zx";
  private static final String PARAM_CLEAR = "clear";
  private static final String PARAM_REFRESH = "refresh";
  private static final String PARAM_BEGIN = "begin";
  private static final String PARAM_END = "end";

  private final String oid;
  private final String zx;
  private final boolean refresh;
  private boolean clear;
  private final Integer begin;
  private final Integer end;

  public ImageUrlParameter(HttpServletRequest request) {
    super(request);
    oid = getUrlParameter(PARAM_OID);
    zx = getUrlParameter(PARAM_ZX);
    refresh = getUrlParameterBoolean(PARAM_REFRESH);
    clear = getUrlParameterBoolean(PARAM_CLEAR);
    begin = getUrlParameterInteger(PARAM_BEGIN);
    end = getUrlParameterInteger(PARAM_END);
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

  public boolean isClear() {
    return clear;
  }

  public Integer getBegin() {
    return begin;
  }

  public Integer getEnd() {
    return end;
  }
}

package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.util.DateUtil;

import javax.servlet.http.HttpServletRequest;

public class ImageUrlParameter extends AbstractUrlParameter {
  private static final String PARAM_OID = "oid";
  private static final String PARAM_ZX = "zx";
  private static final String PARAM_FORMAT = "format";
  private static final String PARAM_SHEET = "sheet";
  private static final String PARAM_CLEAR = "clear";
  private static final String PARAM_REFRESH = "refresh";
  private static final String PARAM_BEGIN = "begin";
  private static final String PARAM_END = "end";

  private final String oid;
  private final String zx;
  private final String format;
  private final boolean refresh;
  private final Integer sheet;
  private boolean clear;
  private final Integer begin;
  private final Integer end;

  public ImageUrlParameter(HttpServletRequest request, DateUtil dateUtil) {
    super(request, dateUtil);
    oid = getUrlParameter(PARAM_OID);
    zx = getUrlParameter(PARAM_ZX);
    format = getUrlParameter(PARAM_FORMAT);
    refresh = getUrlParameterBoolean(PARAM_REFRESH);
    clear = getUrlParameterBoolean(PARAM_CLEAR);
    begin = getUrlParameterInteger(PARAM_BEGIN);
    end = getUrlParameterInteger(PARAM_END);
    sheet = getUrlParameterInteger(PARAM_SHEET, 0);
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

  public String getFormat() {
    return format;
  }

  public Integer getSheet() {
    return sheet;
  }
}

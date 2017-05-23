package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class UrlParameter extends AbstractUrlParameter {
  private static final String PARAM_BEGIN = "begin";
  private static final String PARAM_END = "end";
  private static final String PARAM_SECRET = "secret";
  private static final String PARAM_NEW = "new";
  private static final String PARAM_EXTENDED = "ext";
  private static final String PARAM_TYPE = "type";
  private static final String PARAM_LOCAL_TIMEZONE = "local_timezone";

  private final DataType type;
  private final Date begin;
  private final Date end;
  private final String secret;

  private final boolean extended;
  private final boolean localTimezone;
  protected boolean newFormat;

  public UrlParameter(final HttpServletRequest request, DateUtil dateUtil, DataType defaultDataType) {
    super(request, dateUtil);
    this.type = getUrlParameterType(PARAM_TYPE, defaultDataType);
    this.localTimezone = getUrlParameterBoolean(PARAM_LOCAL_TIMEZONE);
    this.begin = getUrlParameterDate(PARAM_BEGIN, localTimezone);
    this.end = getUrlParameterDate(PARAM_END, localTimezone);
    this.secret = getUrlParameter(PARAM_SECRET);
    this.newFormat = getUrlParameterBoolean(PARAM_NEW);
    this.extended = getUrlParameterBoolean(PARAM_EXTENDED);
  }

  public Date getBegin() {
    return begin;
  }

  public Date getEnd() {
    return end;
  }

  public String getSecret() {
    return secret;
  }

  public boolean isExtended() {
    return extended;
  }

  public boolean isNewFormat() {
    return newFormat;
  }

  public DataType getType() {
    return type;
  }

  public boolean isLocalTimezone() {
    return localTimezone;
  }
}

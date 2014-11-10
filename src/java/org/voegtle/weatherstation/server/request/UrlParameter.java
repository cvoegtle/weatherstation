package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class UrlParameter extends AbstractUrlParameter {
  private static final String PARAM_BEGIN = "begin";
  private static final String PARAM_END = "end";
  private static final String PARAM_SECRET = "secret";

  private final Date begin;
  private final Date end;
  private final String secret;

  public UrlParameter(final HttpServletRequest request) {
    super(request);
    this.begin = getUrlParameterDate(PARAM_BEGIN);
    this.end = getUrlParameterDate(PARAM_END);
    this.secret = getUrlParameter(PARAM_SECRET);
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

}

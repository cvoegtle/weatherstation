package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;

public class RepairUrlParameter  extends UrlParameter {
  private static final String PARAM_SECRET = "secret";
  private final String secret;

  public RepairUrlParameter(HttpServletRequest request) {
    super(request);
    this.secret = getUrlParameter(PARAM_SECRET);
  }

  public String getSecret() {
    return secret;
  }
}

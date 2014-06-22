package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;

public class IncomingUrlParameter extends AbstractUrlParameter {
  private static final String PARAM_DATA = "data";
  private static final String PARAM_LOCATION = "location";
  private static final String PARAM_SECRET = "secret";


  private final String data;
  private final String location;
  private final String secret;

  public IncomingUrlParameter(final HttpServletRequest request) {
    super(request);

    this.data = getUrlParameter(PARAM_DATA);
    this.location = getUrlParameter(PARAM_LOCATION);
    this.secret = getUrlParameter(PARAM_SECRET);
  }

  public String getData() {
    return data;
  }

  public String getLocation() {
    return location;
  }

  public String getSecret() {
    return secret;
  }
}

package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;

public class OutgoingUrlParameter extends UrlParameter {

  public OutgoingUrlParameter(HttpServletRequest request) {
    super(request);
  }
}

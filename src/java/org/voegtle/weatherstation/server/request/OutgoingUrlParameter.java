package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.util.DateUtil;

import javax.servlet.http.HttpServletRequest;

public class OutgoingUrlParameter extends UrlParameter {

  public OutgoingUrlParameter(HttpServletRequest request, DateUtil dateUtil) {
    super(request, dateUtil, null);
  }
}

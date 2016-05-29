package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.util.DateUtil;

import javax.servlet.http.HttpServletRequest;

public class RepairUrlParameter extends UrlParameter {

  public RepairUrlParameter(HttpServletRequest request, DateUtil dateUtil) {
    super(request, dateUtil, null);
  }
}

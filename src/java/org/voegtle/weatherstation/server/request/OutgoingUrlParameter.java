package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;

public class OutgoingUrlParameter extends UrlParameter {
  public static final String PARAM_TYPE = "type";
  private static final String PARAM_EXTENDED = "ext";

  private DataType type;
  private boolean extended;

  public OutgoingUrlParameter(HttpServletRequest request) {
    super(request);
    this.type = getUrlParameterType(PARAM_TYPE);
    this.extended = getUrlParameterBoolean(PARAM_EXTENDED);
  }

  public DataType getType() {
    return type;
  }

  public boolean isExtended() {
    return extended;
  }
}

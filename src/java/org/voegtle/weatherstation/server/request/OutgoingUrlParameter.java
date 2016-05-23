package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;

public class OutgoingUrlParameter extends UrlParameter {
  private static final String PARAM_TYPE = "type";
  private static final String PARAM_EXTENDED = "ext";
  private static final String PARAM_NEW = "new";

  private final DataType type;
  private final boolean extended;
  private final boolean newFormat;

  public OutgoingUrlParameter(HttpServletRequest request) {
    super(request);
    this.type = getUrlParameterType(PARAM_TYPE);
    this.extended = getUrlParameterBoolean(PARAM_EXTENDED);
    this.newFormat = getUrlParameterBoolean(PARAM_NEW);
  }

  public DataType getType() {
    return type;
  }

  public boolean isExtended() {
    return extended;
  }

  public boolean isNewFormat() {
    return newFormat;
  }
}

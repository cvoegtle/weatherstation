package org.voegtle.weatherstation.server.request;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CentralUrlParameter extends UrlParameter {
  private static final String PARAM_EXTENDED = "ext";
  private static final String PARAM_LOCATIONS = "locations";
  private static final String PARAM_TYPE = "type";

  private final boolean extended;
  private final DataType type;
  private final List<String> locations;

  public CentralUrlParameter(HttpServletRequest request) {
    super(request);
    this.extended = getUrlParameterBoolean(PARAM_EXTENDED);
    this.type = getUrlParameterType(PARAM_TYPE, DataType.CURRENT);
    String locationsStr = getUrlParameter(PARAM_LOCATIONS);
    locations = new ArrayList<>();
    Collections.addAll(locations, locationsStr.split(Pattern.quote(",")));

  }

  public boolean isExtended() {
    return extended;
  }

  public List<String> getLocations() {
    return locations;
  }

  public DataType getType() {
    return type;
  }
}

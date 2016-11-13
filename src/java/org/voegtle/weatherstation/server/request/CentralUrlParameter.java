package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class CentralUrlParameter extends UrlParameter {
  private static final String PARAM_UTF8 = "utf8";
  private static final String PARAM_BUILD = "build";
  private static final String PARAM_LOCATIONS = "locations";

  private final boolean utf8;
  private final String buildNumber;
  private final List<String> locations;

  public CentralUrlParameter(HttpServletRequest request) {
    super(request, new DateUtil(TimeZone.getDefault()), DataType.CURRENT);
    this.buildNumber = getUrlParameter(PARAM_BUILD);
    this.utf8 = getUrlParameterBoolean(PARAM_UTF8) || StringUtil.isNotEmpty(buildNumber);
    this.newFormat |= StringUtil.isNotEmpty(buildNumber);

    String locationsStr = getUrlParameter(PARAM_LOCATIONS);
    locations = new ArrayList<>();
    Collections.addAll(locations, locationsStr.split(Pattern.quote(",")));

  }

  public boolean isUtf8() {
    return utf8;
  }

  public List<String> getLocations() {
    return locations;
  }

  public String getBuildNumber() {
    return buildNumber;
  }
}

package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.persistence.WeatherLocation;
import org.voegtle.weatherstation.server.util.StringUtil;

import java.net.MalformedURLException;
import java.net.URL;

public class WeatherUrl {
  private URL url;

  public WeatherUrl(String host, DataType type, boolean extended) throws MalformedURLException {
    url = new URL("https://" + host + "/weatherstation/query?type=" + type.toString() + (extended ? "&ext" : ""));
  }

  public WeatherUrl(WeatherLocation location, UrlParameter param) throws MalformedURLException {
    boolean forwardSecret = StringUtil.isNotEmpty(param.getSecret()) && location.isForwardSecret();
    url = new URL("https://" + location.getHost() + "/weatherstation/query?type=" + param.getType()
        + (param.isExtended() ? "&ext" : "") + (param.isNewFormat() ? "&new" : "")
        + (forwardSecret ? "&secret=" + StringUtil.urlEncode(param.getSecret()) : ""));
  }

  public URL getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return url.toString();
  }
}

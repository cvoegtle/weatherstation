package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.persistence.WeatherLocation;
import org.voegtle.weatherstation.server.util.StringUtil;

import java.net.MalformedURLException;
import java.net.URL;

public class WeatherUrl {
  private URL url;

  public WeatherUrl(String host, DataType type, boolean extended) throws MalformedURLException {
    url = new URL("http://" + host + "/weatherstation/query?type=" + type.toString() + (extended ? "&ext" : ""));
  }

  public WeatherUrl(WeatherLocation location, boolean extended, String secret) throws MalformedURLException {
    boolean forwardSecret = StringUtil.isNotEmpty(secret) && location.isForwardSecret();
    url = new URL("http://" + location.getHost() + "/weatherstation/query?type=" + DataType.CURRENT
        + (extended ? "&ext" : "")
        + (forwardSecret ? "&secret=" + StringUtil.urlEncode(secret) : ""));
  }

  public URL getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return url.toString();
  }
}

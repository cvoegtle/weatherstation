package org.voegtle.weatherstation.server.request;

import java.net.MalformedURLException;
import java.net.URL;

public class WeatherUrl {
  private URL url;

  public WeatherUrl(String host, DataType type, boolean extended) throws MalformedURLException {
    url = new URL("http://" + host + "/weatherstation/query?type=" + type.toString() + (extended ? "&ext" : ""));
  }

  public URL getUrl() {
    return url;
  }
}

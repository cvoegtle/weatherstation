package org.voegtle.weatherstation.server;

import org.json.JSONObject;
import org.voegtle.weatherstation.server.persistence.WeatherLocation;
import org.voegtle.weatherstation.server.request.CentralUrlParameter;
import org.voegtle.weatherstation.server.request.WeatherUrl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class CentralServlet extends AbstractServlet {
  private static final int TIMEOUT = 10000;
  HashMap<String, WeatherLocation> locations;

  @Override
  public void init() throws ServletException {
    super.init();

    locations = pm.fetchWeatherLocations();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Access-Control-Allow-Origin", "*");

    CentralUrlParameter param = new CentralUrlParameter(request);

    ArrayList<JSONObject> collectedWeatherData = new ArrayList<>();

    for (String locationIdentifier : param.getLocations()) {
      log.info("Location: " + locationIdentifier);
      WeatherLocation location = locations.get(locationIdentifier);
      if (location != null) {
        WeatherUrl url = new WeatherUrl(location, param);
        log.info("fetch data from " + url);
        fetchWeatherData(collectedWeatherData, url);
      }
    }

    writeResponse(response, collectedWeatherData, param.isUtf8() ? "UTF-8" : "ISO-8859-1");

  }

  private void fetchWeatherData(ArrayList<JSONObject> collectedWeatherData, WeatherUrl weatherUrl) {
    try {
      JSONObject current = getWeatherDataFromUrl(weatherUrl.getUrl());
      collectedWeatherData.add(current);
    } catch (Exception ex) {
      log.severe(ex.toString());
    }
  }

  private JSONObject getWeatherDataFromUrl(URL url) throws Exception {
    StringBuilder received = new StringBuilder();
    URLConnection connection = url.openConnection();
    connection.setConnectTimeout(TIMEOUT);
    InputStream in = connection.getInputStream();

    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
    String line;
    while ((line = reader.readLine()) != null) {
      received.append(line);
    }
    log.info("response: <" + received + ">");
    return new JSONObject(received.toString());
  }

}

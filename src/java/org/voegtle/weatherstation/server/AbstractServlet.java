package org.voegtle.weatherstation.server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.WeatherLocation;
import org.voegtle.weatherstation.server.util.HashService;
import org.voegtle.weatherstation.server.util.JSONConverter;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractServlet extends HttpServlet {
  protected static final Logger log = Logger.getLogger("ServletLogger");

  private static final String MIME_TYPE_APPLICATION_JSON = "application/json";

  protected final PersistenceManager pm = new PersistenceManager();
  protected LocationProperties locationProperties;
  protected JSONConverter jsonConverter;

  @Override
  public void init() throws ServletException {
    super.init();

//    WeatherLocation location = createWeatherLocation();
//    pm.makePersistant(location);

//    LocationProperties lp = createLocationProperties();
//    pm.makePersistant(lp);

    locationProperties = pm.fetchLocationProperties();
    jsonConverter = new JSONConverter(locationProperties);
  }

  private LocationProperties createLocationProperties() {
    LocationProperties lp = new LocationProperties();
    lp.setLocation("elb");
    lp.setAddress("Karl-Schurz-Str");
    lp.setCityShortcut("MD");
    lp.setCity("Magdeburg");
    lp.setWeatherForecast("");
    lp.setSecretHash("2fe3974d34634baf28c732f4793724f11e4a0813a84030f962187b3844485ae4");
    lp.setReadHash("not set");
    return lp;
  }

  private WeatherLocation createWeatherLocation() {
    WeatherLocation location = new WeatherLocation();
    location.setLocation("elbwetter");
    location.setHost("elbwetter.appspot.com");
    location.setForwardSecret(false);

    return location;
  }

  protected void returnDetailedResult(HttpServletResponse response, List<SmoothedWeatherDataSet> list, boolean extended) {
    List<JSONObject> jsonObjects = jsonConverter.toJson(list, extended);
    writeResponse(response, jsonObjects);
  }

  protected void writeResponse(HttpServletResponse response, JSONObject jsonObject) {
    try {
      PrintWriter out = response.getWriter();
      response.setContentType(MIME_TYPE_APPLICATION_JSON);
      String responseString = jsonObject.toString();
      log.info(responseString);
      out.write(responseString);
      out.close();
    } catch (IOException e) {
      log.severe("Could not write response.");
    }
  }

  protected void writeResponse(HttpServletResponse response, List<JSONObject> jsonObjects) {
    writeResponse(response, jsonObjects, "ISO-8859-1");
  }

  protected void writeResponse(HttpServletResponse response, List<JSONObject> jsonObjects, String encoding) {
    JSONArray jsonArray = new JSONArray(jsonObjects);
    try {
      response.setCharacterEncoding(encoding);
      response.setContentType(MIME_TYPE_APPLICATION_JSON);
      PrintWriter out = response.getWriter();
      String responseString = jsonArray.toString();
      log.info(responseString);
      out.write(responseString);
      out.close();
    } catch (IOException e) {
      log.severe("Could not write response.");
    }
  }

  protected void returnResult(HttpServletResponse response, String result) {
    PrintWriter out;
    try {
      out = response.getWriter();
      response.setContentType("text/plain");
      out.println(result);
      out.close();
    } catch (IOException e) {
      log.severe("Could not write response.");
    }
  }

  protected boolean isCorrectLocation(String location) {
    return StringUtil.isNotEmpty(location) && location.equals(locationProperties.getLocation());
  }

  protected boolean isSecretValid(String secret) {
    String secretHash = locationProperties.getSecretHash();
    return (StringUtil.isEmpty(secretHash) ||
        (StringUtil.isNotEmpty(secret) && secretHash.equals(HashService.calculateHash(secret))));
  }

  protected boolean isReadSecretValid(String secret) {
    String readHash = locationProperties.getReadHash();
    return (StringUtil.isEmpty(readHash) ||
        (StringUtil.isNotEmpty(secret) && readHash.equals(HashService.calculateHash(secret))));
  }

}

package org.voegtle.weatherstation.server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.persistence.*;
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
  LocationProperties locationProperties;
  JSONConverter jsonConverter;

  @Override
  public void init() throws ServletException {
    super.init();

//    WeatherLocation location = createWeatherLocation();
//    pm.makePersistant(location);

//    LocationProperties lp = createLocationProperties();
//    pm.makePersistant(lp);

//    Contact contact = createContact();
//    pm.makePersistant(contact);

    locationProperties = pm.fetchLocationProperties();
    jsonConverter = new JSONConverter(locationProperties);
  }

  private Contact createContact() {
    Contact contact = new Contact();
    contact.setMailAdress("christian@voegtle.org");
    contact.setName("Christian Vögtle");
    contact.setReceiveDailyStatus(true);
    contact.setReceiveIncidentReports(true);
    return contact;
  }

  private LocationProperties createLocationProperties() {
    LocationProperties lp = new LocationProperties();
    lp.setLocation("shenzhen");
    lp.setAddress("Shenzhen");
    lp.setCityShortcut("SZ");
    lp.setCity("Shenzhen");
    lp.setWeatherForecast("");
    lp.setSecretHash("4ac1161eefcfb967e88c54041ac82364327ec75d55390abdfc773c03454572e8");
    lp.setReadHash("not set");
    lp.setIndexOutsideTemperature(19);
    lp.setIndexOutsideHumidity(20);
    lp.setExpectedDataSets(494);
    lp.setExpectedRequests(494);
    return lp;
  }

  private WeatherLocation createWeatherLocation() {
    WeatherLocation location = new WeatherLocation();
    location.setLocation("elbwetter");
    location.setHost("elbwetter.appspot.com");
    location.setForwardSecret(false);

    return location;
  }

  void returnDetailedResult(HttpServletResponse response, List<SmoothedWeatherDataSet> list, boolean extended) {
    List<JSONObject> jsonObjects = jsonConverter.toJson(list, extended);
    writeResponse(response, jsonObjects);
  }

  void writeResponse(HttpServletResponse response, JSONObject jsonObject) {
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

  void writeResponse(HttpServletResponse response, List<JSONObject> jsonObjects) {
    writeResponse(response, jsonObjects, "ISO-8859-1");
  }

  void writeResponse(HttpServletResponse response, List<JSONObject> jsonObjects, String encoding) {
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

  void returnResult(HttpServletResponse response, String result) {
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

  boolean isCorrectLocation(String location) {
    return StringUtil.isNotEmpty(location) && location.equals(locationProperties.getLocation());
  }

  boolean isSecretValid(String secret) {
    String secretHash = locationProperties.getSecretHash();
    return (StringUtil.isEmpty(secretHash) ||
        (StringUtil.isNotEmpty(secret) && secretHash.equals(HashService.calculateHash(secret))));
  }



  boolean isReadSecretValid(String secret) {
    String readHash = locationProperties.getReadHash();
    return isReadSecretValid(readHash, secret);
  }

  boolean isReadSecretValid(String readHash, String secret) {
    return (StringUtil.isEmpty(readHash) ||
        (StringUtil.isNotEmpty(secret) && readHash.equals(HashService.calculateHash(secret))));
  }

}

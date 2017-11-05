package org.voegtle.weatherstation.server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.persistence.*;
import org.voegtle.weatherstation.server.persistence.entities.Contact;
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties;
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.entities.WeatherLocation;
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
    contact.setReceiveDailyStatus(false);
    contact.setReceiveIncidentReports(true);
    return contact;
  }

  private LocationProperties createLocationProperties() {
    LocationProperties lp = new LocationProperties();
    lp.setLocation("development");
    lp.setAddress("Entwicklungs Station");
    lp.setCityShortcut("DEV");
    lp.setCity("Tegelweg 8");
    lp.setWeatherForecast("");
    lp.setSecretHash("2fe3974d34634baf28c732f4793724f11e4a0813a84030f962187b3844485ae4");
    lp.setReadHash("a883d58dbbb62d60da3893c9822d19e43bc371d20ccc5bfdb341f2b120eea54c");
    lp.setIndexInsideTemperature(6);
    lp.setIndexInsideHumidity(14);
    lp.setExpectedDataSets(1000);
    lp.setExpectedRequests(1000);
    lp.setTimezone("Europe/Berlin");

    return lp;
  }

  private WeatherLocation createWeatherLocation() {
    WeatherLocation location = new WeatherLocation();
    location.setLocation("instantwetter");
    location.setHost("instantwetter.appspot.com");
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
        (StringUtil.isNotEmpty(secret) && secretHash.equals(HashService.INSTANCE.calculateHash(secret))));
  }



  boolean isReadSecretValid(String secret) {
    String readHash = locationProperties.getReadHash();
    return isReadSecretValid(readHash, secret);
  }

  boolean isReadSecretValid(String readHash, String secret) {
    return (StringUtil.isEmpty(readHash) ||
        (StringUtil.isNotEmpty(secret) && readHash.equals(HashService.INSTANCE.calculateHash(secret))));
  }

}

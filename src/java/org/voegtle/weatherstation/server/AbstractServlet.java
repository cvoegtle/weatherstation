package org.voegtle.weatherstation.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.HashService;
import org.voegtle.weatherstation.server.util.StringUtil;
import org.voegtle.weatherstation.server.util.WeatherJSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractServlet extends HttpServlet {
  protected static final Logger log = Logger.getLogger("ServletLogger");

  public static final String FORMAT_OUTGOING_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";

  private static final String MIME_TYPE_APPLICATION_JSON = "application/json";

  protected PersistenceManager pm = new PersistenceManager();
  protected LocationProperties locationProperties;

  @Override
  public void init() throws ServletException {
    super.init();

//    LocationProperties lp = createLocationProperties();
//    pm.makePersistant(lp);

    locationProperties = pm.fetchLocationProperties();
  }

  private LocationProperties createLocationProperties() {
    LocationProperties lp = new LocationProperties();
    lp.setLocation("forstweg17");
    lp.setAddress("Forstweg 17");
    lp.setCity("Bonn");
    lp.setWeatherForecast("");
    lp.setSecretHash("4ac1161eefcfb967e88c54041ac82364327ec75d55390abdfc773c03454572e8");
    return lp;
  }

  protected void returnDetailedResult(HttpServletResponse response, List<SmoothedWeatherDataSet> list) {
    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_OUTGOING_TIMESTAMP);

    Integer previousRainCounter = null;
    ArrayList<JSONObject> jsonObjects = new ArrayList<>();
    for (SmoothedWeatherDataSet wds : list) {
      JSONObject json = new WeatherJSONObject();
      try {
        json.put("timestamp", sdf.format(DateUtil.fromGMTtoCEST(wds.getTimestamp())));
        json.put("temperature", wds.getOutsideTemperature());
        json.put("humidity", wds.getOutsideHumidity());
        if (previousRainCounter != null && wds.getRainCounter() != null) {
          double rain = 0.295 * (wds.getRainCounter() - previousRainCounter);
          json.put("rain", Math.max(rain, 0));
        } else {
          json.put("rain", 0.0);
        }
        previousRainCounter = wds.getRainCounter();
        json.put("wind", wds.getWindspeed());
        json.put("windMax", wds.getWindspeedMax());
      } catch (JSONException e) {
        log.log(Level.SEVERE, "failed to create JSONObject", e);
      }
      jsonObjects.add(json);
    }

    writeResponse(response, jsonObjects);
  }

  protected void writeResponse(HttpServletResponse response, JSONObject jsonObject) {
    try {
      PrintWriter out = response.getWriter();
      response.setContentType(MIME_TYPE_APPLICATION_JSON);
      out.write(jsonObject.toString());
      out.close();
    } catch (IOException e) {
      log.severe("Could not write response.");
    }
  }

  protected void writeResponse(HttpServletResponse response, ArrayList<JSONObject> jsonObjects) {
    JSONArray jsonArray = new JSONArray(jsonObjects);
    try {
      PrintWriter out = response.getWriter();
      response.setContentType(MIME_TYPE_APPLICATION_JSON);
      out.write(jsonArray.toString());
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
    return (StringUtil.isEmpty(secretHash) || secretHash.equals(HashService.calculateHash(secret)));
  }

}

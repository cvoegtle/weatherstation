package org.voegtle.weatherstation.server;

import org.json.JSONException;
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO;
import org.voegtle.weatherstation.server.util.JSONConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheServlet extends AbstractInputServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
    try {
     String encodedWeatherData = readString(request);
      CacheWeatherDTO cacheWeatherDTO = new JSONConverter(locationProperties).decodeWeatherDTO(encodedWeatherData);

      pm.makePersistant(cacheWeatherDTO);
    } catch (JSONException e) {
      log.severe("failed to decode CacheWeatherDTO");
    }
  }


}

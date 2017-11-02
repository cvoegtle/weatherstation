package org.voegtle.weatherstation.server;

import org.json.JSONException;
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO;
import org.voegtle.weatherstation.server.util.JSONConverter;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CacheServlet extends AbstractInputServlet {

  private Cache cache;

  @Override
  public void init() throws ServletException {
    super.init();
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      Map properties = new HashMap<>();
      cache = cacheFactory.createCache(properties);
    } catch (CacheException e) {
      log.severe("CacheServlet: Could not instantiate Cache");
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
    try {
      String encodedWeatherData = readString(request);
      log.info("received cache object: <" + encodedWeatherData + ">");
      CacheWeatherDTO cacheWeatherDTO = new JSONConverter(locationProperties).decodeWeatherDTO(encodedWeatherData);

      cache.put(cacheWeatherDTO.getId(), cacheWeatherDTO);

      returnResult(resp, "ACK");
    } catch (JSONException e) {
      log.severe("failed to decode CacheWeatherDTO");
    }
  }


}

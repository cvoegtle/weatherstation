package org.voegtle.weatherstation.server

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import org.json.JSONObject
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO
import org.voegtle.weatherstation.server.persistence.entities.WeatherLocation
import org.voegtle.weatherstation.server.request.CentralUrlParameter
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CentralServlet : AbstractServlet() {
  private val TIMEOUT = 10000

  private var locations: HashMap<String, WeatherLocation>? = null
  private var cache: MemcacheService = createCache()

  @Throws(ServletException::class)
  override fun init() {
    super.init()
    locations = pm.fetchWeatherLocations()
  }

  private fun createCache(): MemcacheService = MemcacheServiceFactory.getMemcacheService()

  @Throws(ServletException::class, IOException::class)
  public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
    response.setHeader("Access-Control-Allow-Origin", "*")

    val param = CentralUrlParameter(request)

    log.info("request from IP " + request.remoteAddr + " with client version=" + param.buildNumber)

    var collectedWeatherData = fetchLocationsFromCache(param)

    writeResponse(response, collectedWeatherData, if (param.isUtf8) "UTF-8" else "ISO-8859-1")
  }

  private fun fetchLocationsFromCache(param: CentralUrlParameter): ArrayList<JSONObject> {
    val result = ArrayList<JSONObject>()

    for (location in param.locations) {
      (cache[location] as CacheWeatherDTO?)?.let {
        sanitize(it, param)
        result.add(jsonConverter!!.toJson(it))
      }
    }
    return result
  }

  private fun sanitize(dto: CacheWeatherDTO, param: CentralUrlParameter) {
    val location = locations!![dto.id]
    if (!isReadSecretValid(location!!.readHash, param.secret)) {
      dto.insideHumidity = null
      dto.insideTemperature = null
    }
  }
}

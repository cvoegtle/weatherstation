package org.voegtle.weatherstation.server

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import org.json.JSONObject
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO
import org.voegtle.weatherstation.server.persistence.entities.WeatherLocation
import org.voegtle.weatherstation.server.request.CentralUrlParameter
import org.voegtle.weatherstation.server.request.DataType
import org.voegtle.weatherstation.server.request.WeatherUrl
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
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

    var collectedWeatherData = ArrayList<JSONObject>()

    if (param.isNewFormat && param.type == DataType.CURRENT) {
      collectedWeatherData = fetchLocationsFromCache(param)
    } else {
      for (locationIdentifier in param.locations) {
        log.info("Location: " + locationIdentifier)
        locations!![locationIdentifier]?.let {
          val url = WeatherUrl(it, param)
          log.info("fetch data from " + url)
          fetchWeatherData(collectedWeatherData, url)
        }
      }
    }

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

  private fun fetchWeatherData(collectedWeatherData: ArrayList<JSONObject>, weatherUrl: WeatherUrl) {
    val current = getWeatherDataFromUrl(weatherUrl.url)
    collectedWeatherData.add(current)
  }

  @Throws(Exception::class)
  private fun getWeatherDataFromUrl(url: URL): JSONObject {
    val received = StringBuilder()
    val connection = url.openConnection()
    connection.connectTimeout = TIMEOUT
    val input = connection.getInputStream()

    val reader = BufferedReader(InputStreamReader(input, "ISO-8859-1"))
    var line: String? = reader.readLine()
    while (line != null) {
      received.append(line)
      line = reader.readLine()
    }
    log.info("response: <$received>")
    return JSONObject(received.toString())
  }
}

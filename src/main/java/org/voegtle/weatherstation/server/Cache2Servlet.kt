package org.voegtle.weatherstation.server

import org.json.JSONException
import org.voegtle.weatherstation.server.util.JSONConverter
import java.io.IOException
import java.util.HashMap
import javax.cache.Cache
import javax.cache.CacheManager
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Cache2Servlet : AbstractInputServlet() {

  private var cache: Cache = createCache()

  @Throws(ServletException::class)
  override fun init() {
    super.init()
  }

  private fun createCache(): Cache {
    val cacheFactory = CacheManager.getInstance().cacheFactory
    return cacheFactory.createCache(HashMap<Any, Any>())
  }

  @Throws(ServletException::class, IOException::class)
  override fun doPost(request: HttpServletRequest, resp: HttpServletResponse) {
    try {
      val encodedWeatherData = readString(request)
      log.info("received cache object: <$encodedWeatherData>")
      val cacheWeatherDTO = JSONConverter(locationProperties!!).decodeWeatherDTO2(encodedWeatherData)

      cache.put(cacheWeatherDTO.id, cacheWeatherDTO)

      returnResult(resp, "ACK")
    } catch (e: JSONException) {
      log.severe("failed to decode CacheWeatherDTO")
    }

  }


}

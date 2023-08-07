package org.voegtle.weatherstation.server

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import org.json.JSONException
import org.voegtle.weatherstation.server.util.JSONConverter
import java.io.IOException
import java.util.logging.Level
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CacheServlet : AbstractInputServlet() {

  private var cache: MemcacheService = createCache()

  @Throws(ServletException::class)
  override fun init() {
    super.init()
  }

  private fun createCache(): MemcacheService = MemcacheServiceFactory.getMemcacheService()

  @Throws(ServletException::class, IOException::class)
  override fun doPost(request: HttpServletRequest, resp: HttpServletResponse) {
    try {
      val encodedWeatherData = readString(request)
      log.info("received cache object: <$encodedWeatherData>")
      val cacheWeatherDTO = JSONConverter(locationProperties!!).decodeWeatherDTO(encodedWeatherData)

      cache.put(cacheWeatherDTO.id, cacheWeatherDTO)

      returnResult(resp, "ACK")
    } catch (e: JSONException) {
      log.log(Level.SEVERE, "failed to decode CacheWeatherDTO", e)
    }

  }


}

package org.voegtle.weatherstation.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.rapid.RapidDataSet
import org.voegtle.weatherstation.server.util.parseUtcDate
import java.util.HashMap
import java.util.logging.Logger
import javax.cache.Cache
import javax.cache.CacheManager


@RestController class RapidService {
  @Autowired
  private val objectMapper: ObjectMapper? = null
  private var cache: Cache = createCache()

  val log = Logger.getLogger("RapidService")

  @GetMapping("/weatherstation/rapid")
  fun receive(@RequestParam ID: String,
              @RequestParam PASSWORD: String,
              @RequestParam dateutc: String,
              @RequestParam temp: Float,
              @RequestParam humidity: Int,
              @RequestParam barometer: Float,
              @RequestParam dailyrain: Float,
              @RequestParam rain: Float,
              @RequestParam UV: Float?,
              @RequestParam solarradiation: Float?,
              @RequestParam winddir: Int?,
              @RequestParam windspeed: Float?,
              @RequestParam windgust: Float?,
              @RequestParam indoortemp: Float?,
              @RequestParam indoorhumidity: Float?): String {
    val dataset = RapidDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer, dailyRain = dailyrain,
                               rain = rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)
    val jsonDataset = objectMapper!!.writerWithDefaultPrettyPrinter().writeValueAsString(dataset)
    cache.set(dataset.time, jsonDataset)
    return cache.size.toString()
  }

  private fun createCache(): Cache {
    val cacheFactory = CacheManager.getInstance().cacheFactory
    return cacheFactory.createCache(HashMap<Any, Any>())
  }
}

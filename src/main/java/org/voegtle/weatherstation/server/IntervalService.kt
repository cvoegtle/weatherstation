package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.logic.WeatherDataAggregator
import org.voegtle.weatherstation.server.logic.WeatherDataForwarder
import org.voegtle.weatherstation.server.logic.WeatherDataSmoother
import org.voegtle.weatherstation.server.util.parseUtcDate
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.time.LocalDateTime
import java.util.Date
import java.util.HashMap
import java.util.logging.Logger
import javax.cache.Cache
import javax.cache.CacheManager

@RestController class IntervalService : AbstractWeewxService(Logger.getLogger("IntervalService")) {
  companion object {
    val minimumIntervall = 90_000
    val LAST_REQUEST_TIME = "last_request"
  }

  private val cache: Cache = createCache()

  @GetMapping("/weatherstation/interval")
  fun receive(@RequestParam ID: String,
              @RequestParam PASSWORD: String,
              @RequestParam dateutc: String,
              @RequestParam temp: Float,
              @RequestParam humidity: Float,
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
    validateReceivedRequest(fetchLocationProperties(), ID, PASSWORD)
    val dataset = WeewxDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer,
                               dailyRain = 12 * dailyrain,
                               rain = 12 * rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)

    if (hasEnoughTimeElapsedSinceLastRequest(dataset.time)) {

      val locationProperties = fetchLocationProperties()
      pm.makePersistant(dataset)
      WeatherDataSmoother(pm, locationProperties.dateUtil).smoothWeatherData()
      WeatherDataAggregator(pm, locationProperties.dateUtil).aggregateWeatherData()
      WeatherDataForwarder(pm, locationProperties).forwardLastDataset()
    }
    return "OK"
  }

  fun hasEnoughTimeElapsedSinceLastRequest(time: Date): Boolean {
    val currentTime = time.time
    val elapsedTime = currentTime - retrieveLastRequest()

    return if (elapsedTime > minimumIntervall) {
      storeLastRequest(currentTime)
      true
    } else {
      log.info("too soon - ignore this dataset")
      false
    }
  }

  private fun retrieveLastRequest() = (cache[LAST_REQUEST_TIME] ?: 0L) as Long

  private fun storeLastRequest(time: Long) {
    cache[LAST_REQUEST_TIME] = time
  }

  private fun createCache(): Cache {
    val cacheFactory = CacheManager.getInstance().cacheFactory
    return cacheFactory.createCache(HashMap<Any, LocalDateTime>())
  }

}

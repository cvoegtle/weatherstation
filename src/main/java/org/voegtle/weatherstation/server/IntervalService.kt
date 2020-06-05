package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.logic.WeatherDataAggregator
import org.voegtle.weatherstation.server.logic.WeatherDataForwarder
import org.voegtle.weatherstation.server.logic.WeatherDataSmoother
import org.voegtle.weatherstation.server.util.parseUtcDate
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.time.Duration
import java.time.LocalDateTime
import java.util.logging.Logger

@RestController class IntervalService : AbstractWeewxService(Logger.getLogger("IntervalService")) {
  companion object {
    var timeOfLastRequest = LocalDateTime.now()
    val minimumIntervall = Duration.ofSeconds(90)
  }

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
    if (hasEnoughTimeElapsedSinceLastRequest()) {
      val dataset = WeewxDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer,
                                 dailyRain = 12 * dailyrain,
                                 rain = 12 * rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                                 windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)

      val locationProperties = fetchLocationProperties()
      pm.makePersistant(dataset)
      WeatherDataSmoother(pm, locationProperties.dateUtil).smoothWeatherData()
      WeatherDataAggregator(pm, locationProperties.dateUtil).aggregateWeatherData()
      WeatherDataForwarder(pm, locationProperties).forwardLastDataset()
    }
    return "OK"
  }

  fun hasEnoughTimeElapsedSinceLastRequest(): Boolean {
    val currentTime = LocalDateTime.now()
    val elapsedTime = Duration.between(timeOfLastRequest, currentTime)

    return if (elapsedTime > minimumIntervall) {
      timeOfLastRequest = currentTime
      true
    } else {
      log.info("too soon - ignore this dataset")
      false
    }

  }
}

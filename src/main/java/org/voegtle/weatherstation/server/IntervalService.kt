package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.logic.WeatherDataAggregator
import org.voegtle.weatherstation.server.logic.WeatherDataForwarder
import org.voegtle.weatherstation.server.logic.WeatherDataSmoother
import org.voegtle.weatherstation.server.request.ResponseCode
import org.voegtle.weatherstation.server.util.parseUtcDate
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.util.logging.Logger

@RestController class IntervalService : AbstractWeewxService(Logger.getLogger("IntervalService")) {
  val intervalChecker = TimeBetweenRequestsChecker("interval_request")

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
              @RequestParam windspeed: Float?, // in m/s
              @RequestParam windgust: Float?, // in m/s
              @RequestParam indoortemp: Float?,
              @RequestParam indoorhumidity: Float?): String {
    validateReceivedRequest(fetchLocationProperties(), ID, PASSWORD)
    val dataset = WeewxDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer,
                               dailyRain = 12 * dailyrain,
                               rain = 12 * rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)

    if (intervalChecker.hasEnoughTimeElapsedSinceLastRequest(dataset.time)) {
      val locationProperties = fetchLocationProperties()
      pm.makePersistent(dataset)
      WeatherDataSmoother(pm, locationProperties.dateUtil).smoothWeatherData()
      WeatherDataAggregator(pm, locationProperties.dateUtil).aggregateWeatherData()
      WeatherDataForwarder(pm, locationProperties).forwardLastDataset()
      return ResponseCode.ACKNOWLEDGE
    } else {
      return ResponseCode.IGNORED
    }
  }

}

package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.logic.WeatherDataForwarder
import org.voegtle.weatherstation.server.logic.WeatherDataSmoother
import org.voegtle.weatherstation.server.util.parseUtcDate
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.util.logging.Logger

@RestController class IntervalService : AbstractWeewxService(Logger.getLogger("IntervalService")) {

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
    val dataset = WeewxDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer, dailyRain = 10*dailyrain,
                               rain = 10*rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)

    val locationProperties = fetchLocationProperties()
    pm.makePersistant(dataset)
    WeatherDataSmoother(pm, locationProperties.dateUtil).smoothWeatherData()
    WeatherDataForwarder(pm, locationProperties).forwardLastDataset()
    return "OK"
  }

}

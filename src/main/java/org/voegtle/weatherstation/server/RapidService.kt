package org.voegtle.weatherstation.server

import com.sun.corba.se.impl.util.RepositoryId.cache
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.util.parseUtcDate
import org.voegtle.weatherstation.server.weewx.AggregatedRapidDataSet
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.util.logging.Logger


@RestController class RapidService : AbstractWeewxService(Logger.getLogger("RapidService")) {

  @GetMapping("/weatherstation/weewx")
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
    val dataset = WeewxDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer, dailyRain = 10*dailyrain,
                               rain = 10*rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)
    var aggregatedDataSet = rapidCache.getLatest()
    if (aggregatedDataSet == null) {
      aggregatedDataSet = AggregatedRapidDataSet(dataset)
    } else {
      aggregatedDataSet.add(dataset)
    }

    storeReceivedDataInCache(aggregatedDataSet)
    return cache.size.toString()
  }

  fun storeReceivedDataInCache(aggregatedDataSet: AggregatedRapidDataSet) {
    rapidCache.save(aggregatedDataSet)
  }

}

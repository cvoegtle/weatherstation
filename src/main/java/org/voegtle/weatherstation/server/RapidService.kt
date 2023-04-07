package org.voegtle.weatherstation.server

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
      val locationProperties = fetchLocationProperties()
      validateReceivedRequest(locationProperties, ID, PASSWORD)
    val dataset = WeewxDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer,
                               dailyRain = locationProperties.rainMultiplier*dailyrain, rain = locationProperties.rainMultiplier*rain,
                               UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)
    var aggregatedDataSet = rapidCache.getLatest()
    if (aggregatedDataSet == null) {
      aggregatedDataSet = AggregatedRapidDataSet(dataset)
    } else {
      aggregatedDataSet.add(dataset)
    }

    storeReceivedDataInCache(aggregatedDataSet)
    return aggregatedDataSet.age().toString()
  }

  fun storeReceivedDataInCache(aggregatedDataSet: AggregatedRapidDataSet) {
    rapidCache.saveLatest(aggregatedDataSet)
  }

}

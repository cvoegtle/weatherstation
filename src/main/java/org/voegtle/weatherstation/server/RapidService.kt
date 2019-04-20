package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@RestController class RapidService {
  val log = Logger.getLogger("RapidService")

  @GetMapping("/weatherstation/rapid")
  fun receive(@RequestParam ID: String,
              @RequestParam PASSWORD: String,
              @RequestParam dateutc: String?,
              @RequestParam temp: Float,
              @RequestParam humidity: Int,
              @RequestParam barometer: Float?,
              @RequestParam dailyrain: Float,
              @RequestParam rain: Float,
              @RequestParam UV: Float?,
              @RequestParam solarradiation: Float?,
              @RequestParam winddir: Int?,
              @RequestParam windspeed: Float?,
              @RequestParam indoortemp: Float?,
              @RequestParam indoorhumidity: Float?,
              @RequestParam windgust: Float?): String {
    log.info("Received at $dateutc for $ID: temperature=$temp, humidity=$humidity, barometer=$barometer")
    return "OK"
  }
}

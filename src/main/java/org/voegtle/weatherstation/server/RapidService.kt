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
              @RequestParam dateutc: String,
              @RequestParam barometer: Float,
              @RequestParam humidity: Int,
              @RequestParam UV: Int,
              @RequestParam solarradiation: Float,
              @RequestParam winddir: Int,
              @RequestParam temp: Float,
              @RequestParam windspeed: Float,
              @RequestParam indoortemp: Float,
              @RequestParam windgust: Float,
              @RequestParam dailyrain: Float,
              @RequestParam rain: Float): String {
    log.warning("Received for $ID: barometer=$barometer")
    return "OK"
  }
}

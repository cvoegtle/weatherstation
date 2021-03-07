package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.request.ResponseCode
import org.voegtle.weatherstation.server.util.parseUtcDate
import org.voegtle.weatherstation.server.weewx.SolarDataSet
import java.util.logging.Logger

@RestController class SolarService : AbstractWeewxService(Logger.getLogger("SolarService")) {
  val intervalChecker = TimeBetweenRequestsChecker("solar_request")

  @GetMapping("/weatherstation/solar")
  fun receive(
    @RequestParam ID: String,
    @RequestParam PASSWORD: String,
    @RequestParam dateutc: String,
    @RequestParam powerProduction: Float,
    @RequestParam totalPowerProduction: Float?,
    @RequestParam powerFeed: Float
  ): String {
    validateReceivedRequest(fetchLocationProperties(), ID, PASSWORD)
    val dataset = SolarDataSet(
      time = parseUtcDate(dateutc),
      powerFeed = powerFeed,
      powerProduction = powerProduction,
      totalPowerProduction = totalPowerProduction ?: 0.0f
    )

    if (intervalChecker.hasEnoughTimeElapsedSinceLastRequest(dataset.time)) {
      pm.makePersistent(dataset)
      return ResponseCode.ACKNOWLEDGE
    } else {
      return ResponseCode.IGNORED
    }
  }

}

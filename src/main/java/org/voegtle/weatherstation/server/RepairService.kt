package org.voegtle.weatherstation.server

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.logic.WeatherDataRepair
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import java.util.Date
import java.util.logging.Logger

@RestController class RepairService : AbstractWeewxService(Logger.getLogger("IntervalService")) {

  @GetMapping("/weatherstation/repair")
  fun receive(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss") begin: Date, 
              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss") end: Date?, @RequestParam secret: String): List<SmoothedWeatherDataSet> {
    val locationProperties = fetchLocationProperties()
    validateReceivedRequest(locationProperties, secret)
    val repairedDatasets = WeatherDataRepair(pm, locationProperties).repair(begin, end ?: Date())
    return repairedDatasets
  }

}

package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher
import java.util.logging.Logger

@RestController class QueryService : AbstractWeewxService(Logger.getLogger("QueryService")) {
  @GetMapping("/weatherstation/query")
  fun query(@RequestParam build: String): UnformattedWeatherDTO {
    log.info("received Request")
    val dataFetcher = WeatherDataFetcher(pm, fetchLocationProperties())
    val dataset = dataFetcher.getLatestWeatherDataUnformatted(false)
    return dataset
  }
}

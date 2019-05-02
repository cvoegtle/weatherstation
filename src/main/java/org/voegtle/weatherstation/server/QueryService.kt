package org.voegtle.weatherstation.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher
import org.voegtle.weatherstation.server.util.JSONConverter
import java.util.logging.Logger

@RestController class QueryService : AbstractWeewxService(Logger.getLogger("QueryService")) {
  @GetMapping("/weatherstation/current")
  fun current(): UnformattedWeatherDTO {
    log.info("received Request")
    val dataFetcher = WeatherDataFetcher(pm, fetchLocationProperties())
    val dataset = dataFetcher.getLatestWeatherDataUnformatted(false)
    return dataset
  }

  @GetMapping("/weatherstation/query")
  fun statistics(): String {
    val dataFetcher = WeatherDataFetcher(pm, fetchLocationProperties())
    val statistics = dataFetcher.fetchStatistics()
    val json = JSONConverter(fetchLocationProperties()).toJson(statistics)
    return json.toString()
  }
}

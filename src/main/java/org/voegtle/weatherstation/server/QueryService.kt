package org.voegtle.weatherstation.server

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.JSONConverter
import java.util.Date
import java.util.logging.Logger

@RestController class QueryService : AbstractWeewxService(Logger.getLogger("QueryService")) {
  @GetMapping("/weatherstation/current")
  fun current(): UnformattedWeatherDTO {
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

  @GetMapping("/weatherstation/list")
  fun list(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") begin: Date,
           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") end: Date): List<SmoothedWeatherDataSet> {
    val dataFetcher = WeatherDataFetcher(pm, fetchLocationProperties())
    return dataFetcher.fetchSmoothedWeatherData(begin, end)
  }
  
  @GetMapping("/weatherstation/aggregated")
  fun aggregatedList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") begin: Date,
           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") end: Date?): List<AggregatedWeatherDataSet> {
    val dataFetcher = WeatherDataFetcher(pm, fetchLocationProperties())
    return dataFetcher.fetchAggregatedWeatherData(begin, end)
  }
  
}

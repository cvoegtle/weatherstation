package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.util.Date
import java.util.LinkedHashMap

class WeatherDataFetcher(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {

  private val dateUtil: DateUtil = locationProperties.dateUtil

  fun getAggregatedWeatherData(begin: Date, end: Date?): List<AggregatedWeatherDataSet> =
      if (end != null) pm.fetchAggregatedWeatherDataInRange(begin, end) else pm.fetchAggregatedWeatherDataInRange(begin)

  fun fetchSmoothedWeatherData(begin: Date, end: Date?): MutableList<SmoothedWeatherDataSet> =
      pm.fetchSmoothedWeatherDataInRange(begin, end)

  private fun fetchTodaysDataSets(): List<SmoothedWeatherDataSet> {
    val today = dateUtil.today()
    return pm.fetchSmoothedWeatherDataInRange(dateUtil.fromLocalToGMT(today), null)
  }

  fun getLatestWeatherDataUnformatted(authorized: Boolean): UnformattedWeatherDTO {
    val latest: WeewxDataSet = pm.fetchYoungestDataSet()

    return UnformattedWeatherDTO(time = latest.time,
                                 location = locationProperties.city,
                                 localtime = dateUtil.toLocalTime(latest.time),
                                 temperature = latest.temperature,
                                 humidity = latest.humidity,
                                 barometer = latest.barometer,
                                 solarradiation = latest.solarRadiation,
                                 isRaining = isRaining(latest),
                                 windspeed = if (locationProperties.isWindRelevant) latest.windSpeed else null,
                                 insideTemperature = if (authorized) latest.indoorTemperature else null,
                                 insideHumidity = if (authorized) latest.indoorHumidity else null,
                                 rainLastHour = latest.rain,
                                 rainToday = latest.dailyRain)
  }

  private fun isRaining(latest: WeewxDataSet) = latest.rain > 0.0f

  fun fetchStatistics(): Statistics {
    val stats = Statistics()
    buildHistoricStatistics(stats)
    buildTodaysStatistics(stats)
    return stats
  }

  private fun buildHistoricStatistics(stats: Statistics) {
    val yesterday = dateUtil.yesterday()
    var dataSets: Collection<AggregatedWeatherDataSet> = pm.fetchAggregatedWeatherDataInRangeDesc(dateUtil.daysEarlier(yesterday, 29), yesterday)
    dataSets = removeDuplicates(dataSets)
    var day = 1
    for (dataSet in dataSets) {
      val range = Statistics.TimeRange.byDay(day++)

      val rain = dataSet.dailyRain
      rain?.let {
        stats.addRain(range, rain)
      }

      stats.setTemperature(range, dataSet.outsideTemperatureMax)
      stats.setTemperature(range, dataSet.outsideTemperatureMin)
    }
  }

  private fun removeDuplicates(dataSets: Collection<AggregatedWeatherDataSet>): Collection<AggregatedWeatherDataSet> {
    val reducedList = LinkedHashMap<Date, AggregatedWeatherDataSet>()
    for (dataSet in dataSets) {
      reducedList.put(dataSet.date, dataSet)
    }
    return reducedList.values
  }

  private fun buildTodaysStatistics(stats: Statistics) {
    val todaysDataSets = fetchTodaysDataSets()

    if (todaysDataSets.size > 0) {
      val latest: WeewxDataSet = pm.fetchYoungestDataSet()
      stats.rainLastHour = latest.rain

      stats.addRain(Statistics.TimeRange.today, latest.dailyRain)
      stats.setTemperature(Statistics.TimeRange.today, latest.temperature)

      for (dataSet in todaysDataSets) {
        stats.setTemperature(Statistics.TimeRange.today, dataSet.outsideTemperature)
      }
    }
  }


  fun fetchRainData(): RainDTO {
    val statistics = fetchStatistics()

    return statistics.toRainDTO()
  }

}

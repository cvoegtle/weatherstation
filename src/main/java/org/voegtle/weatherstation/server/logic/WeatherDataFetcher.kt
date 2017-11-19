package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.Date
import java.util.LinkedHashMap

class WeatherDataFetcher(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {
  private val dateUtil: DateUtil = locationProperties.dateUtil

  private fun firstDataSetOfToday(): SmoothedWeatherDataSet? {
      var today: Date = dateUtil.today()
      today = dateUtil.fromLocalToGMT(today)!!
      val oneHourLater = dateUtil.incrementHour(today)
      return pm.fetchOldestSmoothedDataSetInRange(today, oneHourLater)
    }

  fun getAggregatedWeatherData(begin: Date, end: Date): List<AggregatedWeatherDataSet> =
      pm.fetchAggregatedWeatherDataInRange(begin, end)

  fun fetchSmoothedWeatherData(begin: Date, end: Date): MutableList<SmoothedWeatherDataSet> =
      pm.fetchSmoothedWeatherDataInRange(begin, end)

  private fun fetchTodaysDataSets(): List<SmoothedWeatherDataSet> {
    val today = dateUtil.today()
    return pm.fetchSmoothedWeatherDataInRange(dateUtil.fromLocalToGMT(today), null)
  }

  fun getLatestWeatherDataUnformatted(authorized: Boolean): UnformattedWeatherDTO {
    val today = firstDataSetOfToday()
    val latest = pm.fetchYoungestDataSet()
    val twentyMinutesBefore = pm.fetchDataSetMinutesBefore(Date(), 20)
    val oneHourBefore = pm.fetchDataSetMinutesBefore(Date(), 60)

    val dto = UnformattedWeatherDTO()
    dto.time = latest.timestamp
    dto.localTime = dateUtil.toLocalTime(latest.timestamp)
    dto.temperature = latest.outsideTemperature
    dto.humidity = latest.outsideHumidity

    val raining = isRaining(latest, twentyMinutesBefore)
    dto.isRaining = raining

    if (locationProperties.isWindRelevant) {
      dto.windspeed = latest.windspeed
    }
    dto.watt = latest.watt
    if (authorized) {
      dto.insideTemperature = latest.insideTemperature
      dto.insideHumidity = latest.insideHumidity
    }

    if (SmoothedWeatherDataSet.hasRainCounter(oneHourBefore)) {
      dto.rainLastHour = calculateRain(latest!!.rainCounter!!, oneHourBefore!!.rainCounter!!)
    }

    if (SmoothedWeatherDataSet.hasRainCounter(today) && WeatherDataSet.hasRainCounter(latest)) {
      dto.rainToday = calculateRain(latest.rainCounter!!, today!!.rainCounter!!)
    }

    return dto
  }

  private fun isRaining(latest: WeatherDataSet, fifteenMinutesBefore: SmoothedWeatherDataSet?): Boolean {
    var raining = false
    if (latest.isRaining != null) {
      raining = latest.isRaining!!
    }
    if (latest.rainCounter != null && SmoothedWeatherDataSet.hasRainCounter(fifteenMinutesBefore)) {
      raining = raining || latest.rainCounter!! - fifteenMinutesBefore!!.rainCounter!! > 0
    }
    return raining
  }

  fun fetchStatistics(): Statistics {
    val stats = Statistics()
    buildHistoricStatistics(stats)
    buildTodaysStatistics(stats)
    return stats
  }

  private fun buildHistoricStatistics(stats: Statistics) {
    val yesterday = dateUtil.yesterday()
    var dataSets: Collection<AggregatedWeatherDataSet> = pm.fetchAggregatedWeatherDataInRange(
        dateUtil.daysEarlier(yesterday, 29), yesterday, false)
    dataSets = removeDuplicates(dataSets)
    var day = 1
    for (dataSet in dataSets) {
      val range = Statistics.TimeRange.byDay(day++)

      val rain = calculateRain(dataSet.rainCounter, 0)
      if (rain != null) {
        stats.addRain(range, rain)
      }

      stats.addKwh(range, calculateKwh(dataSet.kwh, 0.0))

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
      val firstSet = todaysDataSets[0]
      val latest = pm.fetchYoungestDataSet()
      val oneHourBefore = pm.fetchDataSetMinutesBefore(Date(), 60)
      stats.rainLastHour = calculateRain(latest, oneHourBefore)

      stats.addRain(Statistics.TimeRange.today, calculateRain(latest, firstSet))
      stats.addKwh(Statistics.TimeRange.today, calculateKwh(latest, firstSet))
      stats.setTemperature(Statistics.TimeRange.today, latest.outsideTemperature)

      for (dataSet in todaysDataSets) {
        stats.setTemperature(Statistics.TimeRange.today, dataSet.outsideTemperature)
      }
    }
  }


  fun fetchRainData(): RainDTO {
    val statistics = fetchStatistics()

    return statistics.toRainDTO()
  }

  private fun calculateRain(latest: WeatherDataSet?, previous: SmoothedWeatherDataSet?): Float? {
    return if (latest == null || previous == null || latest.rainCounter == null || previous.rainCounter == null) {
      null
    } else calculateRain(latest.rainCounter!!, previous.rainCounter!!)
  }

  private fun calculateRain(youngerCount: Int, olderCount: Int): Float? {
    val rainCount = youngerCount - olderCount
    return if (rainCount > 0) {
      (0.295 * rainCount).toFloat()
    } else null
  }

  private fun calculateKwh(latest: WeatherDataSet?, previous: SmoothedWeatherDataSet?): Double? {
    return if (latest == null || previous == null || latest.kwh == null || previous.kwh == null) {
      null
    } else calculateKwh(latest.kwh, previous.kwh)
  }

  private fun calculateKwh(youngerKwh: Double?, olderKwh: Double?): Double? {
    if (youngerKwh != null && olderKwh != null) {
      val kwh = youngerKwh - olderKwh
      return if (kwh > 0) kwh else null
    }
    return null
  }


}

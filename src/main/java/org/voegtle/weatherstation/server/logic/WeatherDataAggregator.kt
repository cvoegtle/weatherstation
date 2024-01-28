package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.logic.caching.CachedWeatherDataProvider
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import java.util.logging.Logger

class WeatherDataAggregator(private val pm: PersistenceManager, private val dateUtil: DateUtil) {
  private val weatherDataProvider = CachedWeatherDataProvider(pm, dateUtil)
  private val log = Logger.getLogger(WeatherDataAggregator::class.java.name)

  fun aggregateWeatherData() {
    var dateOfLastAggregation = fetchDateOfLastAggregation()
    val dateOfLastWeatherDataSet = fetchLastDateWithCompleteWeatherDataSets()

    while (dateUtil.isClearlyBefore(dateOfLastAggregation, dateOfLastWeatherDataSet)) {
      val aggregatedDay = createNewDay(dateOfLastAggregation)
      log.info("aggregate " + aggregatedDay.date)
      val weatherDataSets = pm.fetchSmoothedWeatherDataInRange(dateUtil.fromLocalToGMT(aggregatedDay.date),
                                                               dateUtil.fromLocalToGMT(dateUtil.nextDay(aggregatedDay.date)))

      aggregate(aggregatedDay, weatherDataSets)

      pm.makePersistent(aggregatedDay)
      dateOfLastAggregation = aggregatedDay.date
    }
  }

  private fun aggregate(aggregation: AggregatedWeatherDataSet, weatherDataSets: List<SmoothedWeatherDataSet>) {
    if (weatherDataSets.isNotEmpty()) {
      var rainCountStart: Int? = null
      var rainCountLast: Int? = null

      for (wds in weatherDataSets) {
        if (wds.isValid) {
          aggregation.addOutsideTemperature(wds.outsideTemperature, wds.timestamp)
          aggregation.addOutsideHumidity(wds.outsideHumidity)
          aggregation.addInsideTemperature(wds.insideTemperature)
          aggregation.addInsideHumidity(wds.insideHumidity)
          aggregation.updatePowerProductionMax(wds.powerProductionMax, wds.timestamp)
          if (rainCountStart == null) {
            rainCountStart = wds.rainCounter
          }
          rainCountLast = wds.rainCounter
        }
      }

      aggregation.totalPowerProduction = calculateDailyPowerProduction(weatherDataSets)

      rainCountStart?.let {
        val lastCount = (rainCountLast ?: 0)
        val referenceCount = makeOverflowCorrection(it, lastCount)
        aggregation.rainCounter = Math.max(lastCount - referenceCount, 0)
        aggregation.rainDays = if (lastCount > referenceCount) 1 else 0
      }

      aggregation.normalize()
    }
    aggregation.isFinished = true
  }

  private fun calculateDailyPowerProduction(weatherDataSets: List<SmoothedWeatherDataSet>): Float? {
    var totalPowerProductionInitial = weatherDataSets.first().totalPowerProduction
    var totalPowerProductionFinal = weatherDataSets.last().totalPowerProduction
    if (totalPowerProductionInitial != null && totalPowerProductionFinal != null) {
      return (totalPowerProductionFinal - totalPowerProductionInitial).coerceAtLeast(0.0f)
    }
    return null
  }


  private fun fetchDateOfLastAggregation(): Date {
    val lastAggregatedDay = pm.fetchYoungestAggregatedDataSet()
    return lastAggregatedDay?.date ?: dateUtil.getDate(2023, 10, 1)
  }

  private fun fetchLastDateWithCompleteWeatherDataSets(): Date {
    val youngest = weatherDataProvider.getYoungestWeatherDataSet()
    var timestamp = dateUtil.daysEarlier(youngest.timestamp, 1)
    timestamp = dateUtil.fromGMTtoLocal(timestamp)
    return timestamp
  }

  private fun createNewDay(lastDay: Date): AggregatedWeatherDataSet {
    return AggregatedWeatherDataSet(dateUtil.incrementDay(lastDay))
  }

}

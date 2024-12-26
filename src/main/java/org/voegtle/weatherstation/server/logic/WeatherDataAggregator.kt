package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import java.util.logging.Logger

class WeatherDataAggregator(private val pm: PersistenceManager, private val dateUtil: DateUtil) {
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
      for (wds in weatherDataSets) {
        if (wds.isValid) {
          aggregation.addOutsideTemperature(wds.outsideTemperature, wds.timestamp)
          aggregation.addOutsideHumidity(wds.outsideHumidity)
          aggregation.addInsideTemperature(wds.insideTemperature)
          aggregation.addInsideHumidity(wds.insideHumidity)
          aggregation.addWindspeed(wds.windspeed)
          aggregation.updateWindspeedMax(wds.windspeedMax)
          aggregation.addSolarRadiation(wds.solarRadiation)
          aggregation.updateSolarRadiationMax(wds)
          aggregation.totalPowerProduction = wds.totalPowerProduction
          aggregation.updatePowerProductionMax(wds.powerProductionMax, wds.timestamp)
          aggregation.dailyRain = wds.dailyRain
        }
      }
      aggregation.totalPowerProduction = calculateDailyPowerProduction(weatherDataSets)
      aggregation.normalize()
    }
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
    return lastAggregatedDay?.date ?: dateUtil.getDate(2021, 1, 1)
  }

  private fun fetchLastDateWithCompleteWeatherDataSets(): Date {
    val youngest = pm.fetchYoungestDataSet()
    var timestamp = dateUtil.daysEarlier(youngest.time, 1)
    timestamp = dateUtil.fromGMTtoLocal(timestamp)
    return timestamp
  }

  private fun createNewDay(lastDay: Date): AggregatedWeatherDataSet {
    return AggregatedWeatherDataSet(date = dateUtil.incrementDay(lastDay))
  }

}

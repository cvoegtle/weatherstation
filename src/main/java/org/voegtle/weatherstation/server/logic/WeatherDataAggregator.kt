package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.Date
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

      pm.makePersistant(aggregatedDay)
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
          aggregation.updateSolarRadiationMax(wds.solarRadiationMax, wds.timestamp)
          aggregation.dailyRain = wds.dailyRain
        }
      }

      aggregation.normalize()
    }
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

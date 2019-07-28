package org.voegtle.weatherstation.server.util

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import java.util.Date
import java.util.logging.Logger

class UpgradeService(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {
  protected val log = Logger.getLogger("UpgradeService")

  fun upgradeAggregated() {
    val startDate = locationProperties.dateUtil.getDate(2018, 7, 1)
    val aggregatedWeatherData = pm.fetchAggregatedWeatherDataInRange(startDate, null)
    aggregatedWeatherData.forEach {
      it.dailyRain = it.rainCounter * 0.295f
      log.info(it.toString())
      pm.upgradeAggregatedDataset(it)
    }
  }

  fun upgradeSmoothed(startDate: Date, end: Date?) {
    val smoothedWeatherData = pm.fetchSmoothedWeatherDataInRange(startDate, end)
    var previousRainCounter: Int? = null
    smoothedWeatherData.forEach {
      val currentRainCounter = it.rainCounter
      if (previousRainCounter != null && currentRainCounter != null && currentRainCounter > previousRainCounter!!) {
        it.dailyRain = 0.295f * (currentRainCounter - previousRainCounter!!)
      } else {
        it.dailyRain = 0.0f
      }
      log.info(it.toString())
      pm.updateSmoothedDataset(it)
      previousRainCounter = currentRainCounter ?: previousRainCounter
    }
  }
}

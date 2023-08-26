package org.voegtle.weatherstation.server.util

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import java.util.*
import java.util.logging.Logger

class UpgradeService(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {
  protected val log = Logger.getLogger("UpgradeService")

  fun upgradeAggregated() {
    val startDate = locationProperties.dateUtil.getDate(2018, 7, 1)
    val aggregatedWeatherData = pm.fetchAggregatedWeatherDataInRange(startDate)
    aggregatedWeatherData.forEach {
      it.dailyRain = it.rainCounter * 0.295f
      log.info(it.toString())
      pm.makePersitant(it)
    }
  }

  fun upgradeSmoothed(startDate: Date, end: Date?) {
    val smoothedWeatherData = pm.fetchSmoothedWeatherDataInRange(startDate, end)
    var rainCounterOfFirstDataSetOfDay: Int = 0
    smoothedWeatherData.forEach {
      it.rainCounter?.let { currentRainCounter ->
        if (currentRainCounter > rainCounterOfFirstDataSetOfDay) {
          it.dailyRain = 0.295f * (currentRainCounter - rainCounterOfFirstDataSetOfDay)
        } else {
          it.dailyRain = 0.0f
        }
        log.info(it.toString())
        pm.makePersitant(it)
        rainCounterOfFirstDataSetOfDay = if (isNewDay(it.timestamp)) currentRainCounter else rainCounterOfFirstDataSetOfDay
      }
    }
  }

  fun isNewDay(date: Date): Boolean {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0
  }
}

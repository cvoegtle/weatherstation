package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.logic.caching.CachedWeatherDataProvider
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*

internal class WeatherDataSmoother(private val pm: PersistenceManager, private val dateUtil: DateUtil) {
  private val weatherDataProvider = CachedWeatherDataProvider(pm, dateUtil)

  fun smoothWeatherData() {
    val endTime = calculateEndTime()

    var currentTime = calculateStartTime(endTime)
    while (currentTime.before(endTime)) {
      val range = dateUtil.getRangeAround(currentTime, 7 * 60 + 30)
      val weatherData = pm.fetchWeatherDataInRange(range.begin, range.end)

      val smoothed = SmoothedWeatherDataSet(currentTime)
      weatherData.forEach { wds -> smoothed.add(wds) }
      smoothed.normalize()
      weatherDataProvider.write(smoothed)
      pm.removeWeatherDataInRange(range.begin, range.end)

      currentTime = dateUtil.incrementDateBy15min(currentTime)
    }
  }

  private fun calculateStartTime(timeOfYoungestWeatherDataSet: Date): Date {
    val youngest = weatherDataProvider.getYoungestSmoothedWeatherDataSet()

    val cal = Calendar.getInstance(Locale.GERMANY)

    if (youngest != null) {
      cal.time = youngest.timestamp
      cal.add(Calendar.MINUTE, 15)
    } else {
      cal.time = timeOfYoungestWeatherDataSet
      cal.add(Calendar.HOUR_OF_DAY, -1)
      cal.set(Calendar.MINUTE, 0)
    }

    return cal.time
  }

  private fun calculateEndTime(): Date {
    val youngest = weatherDataProvider.getYoungestWeatherDataSet()
    val cal = Calendar.getInstance(Locale.GERMANY)
    cal.time = youngest.timestamp
    cal.add(Calendar.MINUTE, -8)
    return cal.time
  }
}

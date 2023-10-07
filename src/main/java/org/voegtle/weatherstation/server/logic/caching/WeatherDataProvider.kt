package org.voegtle.weatherstation.server.logic.caching

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*

class WeatherDataProvider(private val pm: PersistenceManager) {
    private var cache: Cache = Cache()

    fun getYoungestWeatherDataSet(): WeatherDataSet {
        var youngest = cache[CacheKey.YoungestWeatherDataSet] as WeatherDataSet?
        if (youngest == null) {
            youngest = pm.fetchYoungestDataSet()
            cache[CacheKey.YoungestWeatherDataSet] = youngest
        }
        return youngest
    }

    fun write(weatherDataSet: WeatherDataSet) {
        pm.makePersistent(weatherDataSet)
        cache[CacheKey.YoungestWeatherDataSet] = weatherDataSet
    }

    fun getSmoothedWeatherDataSetMinutesBefore(minutesBefore: Int): SmoothedWeatherDataSet {
        var smoothedWeatherDataSet = cache.get(CacheKey.SmoothedWeatherDataSet, minutesBefore) as SmoothedWeatherDataSet?
        if (smoothedWeatherDataSet == null || isOutDated(smoothedWeatherDataSet, minutesBefore)) {
            smoothedWeatherDataSet = pm.fetchDataSetMinutesBefore(Date(), minutesBefore)
            cache.put(CacheKey.SmoothedWeatherDataSet, minutesBefore, smoothedWeatherDataSet)
        }
        return smoothedWeatherDataSet
    }

    private fun isOutDated(smoothedWeatherDataSet: SmoothedWeatherDataSet, minutesBefore: Int): Boolean {
        val maxAge = DateUtil.minutesBefore(Date(), minutesBefore + 15)
        return smoothedWeatherDataSet.timestamp.before(maxAge)
    }
}

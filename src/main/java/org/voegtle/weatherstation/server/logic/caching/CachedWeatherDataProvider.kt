package org.voegtle.weatherstation.server.logic.caching

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import java.util.logging.Logger

class CachedWeatherDataProvider(private val pm: PersistenceManager, val dateUtil: DateUtil) {
    companion object {
        private val log = Logger.getLogger(CachedWeatherDataProvider::class.java.name)
    }
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

    fun getYoungestSmoothedWeatherDataSet(): SmoothedWeatherDataSet? {
        var youngest = cache[CacheKey.YoungestSmoothedWeatherDataSet] as SmoothedWeatherDataSet?
        if (youngest == null) {
            log.warning("getYoungestSmoothedWeatherDataSet() cache miss")
            youngest = pm.fetchYoungestSmoothedDataSet()
            youngest?.let {
                log.warning("getYoungestSmoothedWeatherDataSet() add to cache: " + it.timestamp)
                cache[CacheKey.YoungestSmoothedWeatherDataSet] = it
            }
        }
        return youngest
    }

    fun write(smoothedWeatherDataSet: SmoothedWeatherDataSet) {
        pm.makePersistent(smoothedWeatherDataSet)
        cache[CacheKey.YoungestSmoothedWeatherDataSet] = smoothedWeatherDataSet
    }


    fun getSmoothedWeatherDataSetMinutesBefore(minutesBefore: Int): SmoothedWeatherDataSet {
        var smoothedWeatherDataSet = cache.get(CacheKey.SmoothedWeatherDataSet, minutesBefore) as SmoothedWeatherDataSet?
        if (smoothedWeatherDataSet == null || isOutDated(smoothedWeatherDataSet, minutesBefore)) {
            smoothedWeatherDataSet = pm.fetchDataSetMinutesBefore(Date(), minutesBefore)
            cache.put(CacheKey.SmoothedWeatherDataSet, minutesBefore, smoothedWeatherDataSet)
        }
        return smoothedWeatherDataSet
    }

    fun getFirstSmoothedWeatherDataSetOfToday(): SmoothedWeatherDataSet? {
        var today: Date = dateUtil.today()
        today = dateUtil.fromLocalToGMT(today)

        var first = cache[CacheKey.FirstSmoothedWeatherDataSetOfToday] as SmoothedWeatherDataSet?
        if (first == null || first.timestamp.before(today)) {
            first = firstDataSetOfToday()
            first?.let {
                cache[CacheKey.FirstSmoothedWeatherDataSetOfToday] = first
            }
        }
        return first
    }

    private fun firstDataSetOfToday(): SmoothedWeatherDataSet? {
        var today: Date = dateUtil.today()
        today = dateUtil.fromLocalToGMT(today)
        val oneHourLater = dateUtil.incrementHour(today)
        return pm.fetchOldestSmoothedDataSetInRange(today, oneHourLater)
    }

    private fun isOutDated(smoothedWeatherDataSet: SmoothedWeatherDataSet, minutesBefore: Int): Boolean {
        val maxAge = DateUtil.minutesBefore(Date(), minutesBefore + 15)
        return smoothedWeatherDataSet.timestamp.before(maxAge)
    }
}

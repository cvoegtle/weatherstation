package org.voegtle.weatherstation.server.logic.caching

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet

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
}

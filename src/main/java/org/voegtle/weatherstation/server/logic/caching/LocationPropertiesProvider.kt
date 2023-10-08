package org.voegtle.weatherstation.server.logic.caching

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties

class LocationPropertiesProvider(private val pm: PersistenceManager) {
    private var cache: Cache = Cache()
    fun getLocationProperties(): LocationProperties {
        var locationProperties = cache[CacheKey.LocationProperties] as LocationProperties?
        if (locationProperties == null) {
            locationProperties = pm.fetchLocationProperties()
            cache[CacheKey.LocationProperties] = locationProperties
        }
        return locationProperties
    }
}

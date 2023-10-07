package org.voegtle.weatherstation.server.logic.caching

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties

class Cache {
    private var memoryCache: MemcacheService = MemcacheServiceFactory.getMemcacheService()

    fun put(key: CacheKey, value: Any) {
        memoryCache.put(key.name, value)
    }

    operator fun get(key: CacheKey): Any? =memoryCache.get(key.name)
    operator fun set(key: CacheKey, value: LocationProperties) {
        put(key, value)
    }
}

package org.voegtle.weatherstation.server.weewx

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties

class RapidCache {
  private var cache: MemcacheService = createCache()
  var objectMapper: ObjectMapper? = null

  private fun createCache(): MemcacheService = MemcacheServiceFactory.getMemcacheService()

  fun getLatest(): AggregatedRapidDataSet? {
    val json: String? = cache.get("latest") as String?
    return if (json != null) objectMapper!!.readValue(json, AggregatedRapidDataSet::class.java) else null
  }

  fun saveLatest(aggregatedDataSet: AggregatedRapidDataSet) {
    val json = objectMapper!!.writeValueAsString(aggregatedDataSet)
    cache.put("latest", json)
    cache.put(aggregatedDataSet.time(), json)
  }

  fun getLocationProperties(): LocationProperties? {
    val jsonLocation = cache.get("location")
    if (jsonLocation != null) {
      return objectMapper!!.readValue(jsonLocation as String, LocationProperties::class.java)
    }
    return null
  }

  fun saveLocationProperties(locationProperties: LocationProperties) {
    val json = objectMapper!!.writeValueAsString(locationProperties)
    cache.put("location", json)
  }

}

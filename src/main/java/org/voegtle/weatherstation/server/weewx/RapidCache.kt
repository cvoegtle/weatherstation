package org.voegtle.weatherstation.server.weewx

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory

class RapidCache {
  private var cache: MemcacheService = createCache()
  var objectMapper: ObjectMapper? = null

  private fun createCache(): MemcacheService = MemcacheServiceFactory.getMemcacheService()

  fun getLatest(): AggregatedRapidDataSet? {
    val json: String? = cache.get("latest") as String?
    return if (json != null) objectMapper!!.readValue(json, AggregatedRapidDataSet::class.java) else null
  }

  fun save(aggregatedDataSet: AggregatedRapidDataSet) {
    val json = objectMapper!!.writeValueAsString(aggregatedDataSet)
    cache.put("latest", json)
    cache.put(aggregatedDataSet.time(), json)
  }


}

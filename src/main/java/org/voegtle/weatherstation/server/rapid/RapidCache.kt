package org.voegtle.weatherstation.server.rapid

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.HashMap
import javax.cache.Cache
import javax.cache.CacheManager

class RapidCache {
  private var cache: Cache = createCache()
  var objectMapper: ObjectMapper? = null

  private fun createCache(): Cache {
    val cacheFactory = CacheManager.getInstance().cacheFactory
    return cacheFactory.createCache(HashMap<Any, String>())
  }

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

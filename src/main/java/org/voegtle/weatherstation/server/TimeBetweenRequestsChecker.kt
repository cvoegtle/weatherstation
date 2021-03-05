package org.voegtle.weatherstation.server

import java.time.LocalDateTime
import java.util.Date
import java.util.HashMap
import java.util.logging.Logger
import javax.cache.Cache
import javax.cache.CacheManager

class TimeBetweenRequestsChecker(val requestIdentifier: String) {
  private val log = Logger.getLogger("TimeBetweenRequestsChecker")
  private val minimumIntervall = 90_000
  private val cache: Cache = createCache()

  public fun hasEnoughTimeElapsedSinceLastRequest(time: Date): Boolean {
    val currentTime = time.time
    val elapsedTime = currentTime - retrieveLastRequest()

    return if (elapsedTime > minimumIntervall) {
      storeLastRequest(currentTime)
      true
    } else {
      log.info("${requestIdentifier} - too soon - ignore this dataset")
      false
    }
  }

  private fun retrieveLastRequest() = (cache[requestIdentifier] ?: 0L) as Long

  private fun storeLastRequest(time: Long) {
    cache[requestIdentifier] = time
  }

  private fun createCache(): Cache {
    val cacheFactory = CacheManager.getInstance().cacheFactory
    return cacheFactory.createCache(HashMap<Any, LocalDateTime>())
  }


}
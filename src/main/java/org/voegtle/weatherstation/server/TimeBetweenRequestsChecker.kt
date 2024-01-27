package org.voegtle.weatherstation.server

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import java.util.*
import java.util.logging.Logger

class TimeBetweenRequestsChecker(val requestIdentifier: String) {
  private val log = Logger.getLogger("TimeBetweenRequestsChecker")
  private val minimumIntervall = 90_000

  private var cache: MemcacheService = createCache()

  private fun createCache(): MemcacheService = MemcacheServiceFactory.getMemcacheService()


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
    cache.put(requestIdentifier, time)
  }


}

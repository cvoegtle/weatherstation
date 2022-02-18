package org.voegtle.weatherstation.server.logic

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import org.voegtle.weatherstation.server.persistence.HealthDTO
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*

class HealthProvider(private val pm: PersistenceManager, locationProperties: LocationProperties) {
  private val HEALTH = "health"
  private val cache: MemcacheService

  private val dateUtil: DateUtil = locationProperties.dateUtil

  init {
    cache = MemcacheServiceFactory.getMemcacheService()
  }

  fun get(): HealthDTO {
    val today = dateUtil.today()
    val health: HealthDTO? = cache[HEALTH] as HealthDTO?
    if (health == null || isOutdated(health, today)) {
      return pm.selectHealth(today).toDTO()
    }
    return health
  }

  operator fun get(day: Date): HealthDTO = pm.selectHealth(day).toDTO()

  fun update(health: HealthDTO) {
    cache.put(HEALTH, health)
    pm.makePersistent(health)
  }

  private fun isOutdated(health: HealthDTO, today: Date): Boolean = today.after(health.day)

}

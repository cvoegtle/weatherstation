package org.voegtle.weatherstation.server.logic.caching

import org.voegtle.weatherstation.server.persistence.HealthDTO
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*

class HealthProvider(private val pm: PersistenceManager, locationProperties: LocationProperties) {
  private val HEALTH = CacheKey.Health
  private var cache: Cache = Cache()

  private val dateUtil: DateUtil = locationProperties.dateUtil

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

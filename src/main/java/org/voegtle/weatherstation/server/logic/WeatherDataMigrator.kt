package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import java.util.*

class WeatherDataMigrator (private val pm: PersistenceManager, private val locationProperties: LocationProperties){
  fun migrateSmoothedWeatherData(begin: Date, end: Date) {
    pm.fetchSmoothedWeatherDataInRange(begin, end)

  }
}

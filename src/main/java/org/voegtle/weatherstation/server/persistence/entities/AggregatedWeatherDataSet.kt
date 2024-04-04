package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Ignore
import com.googlecode.objectify.annotation.Index
import java.util.*

@Entity
class AggregatedWeatherDataSet(@Id private var id: Long? = null,

                               @Index var date: Date = Date(),

                               var timeOfMinimum: Date? = null,
                               var timeOfMaximum: Date? = null,
                               var outsideTemperatureMin: Float? = null,
                               var outsideTemperatureMax: Float? = null,
                               var outsideTemperatureAverage: Float? = null,
                               @Ignore private var outsideTemperatureCounter: Int = 0,

                               var outsideHumidityMin: Float? = null,
                               var outsideHumidityMax: Float? = null,
                               var outsideHumidityAverage: Float? = null,
                               @Ignore private var outsideHumidityCounter: Int = 0,

                               var insideTemperatureMin: Float? = null,
                               var insideTemperatureMax: Float? = null,
                               var insideTemperatureAverage: Float? = null,
                               @Ignore private var insideTemperatureCounter: Int = 0,

                               var insideHumidityMin: Float? = null,
                               var insideHumidityMax: Float? = null,
                               var insideHumidityAverage: Float? = null,
                               @Ignore private var insideHumidityCounter: Int = 0,

                               var windspeedMin: Float? = null,
                               var windspeedMax: Float? = null,
                               var windspeedAverage: Float? = null,
                               @Ignore private var windspeedCounter: Int = 0,

                               var firstSolarRadiation: Date? = null,
                               var lastSolarRadiation: Date? = null,
                               var solarRadiationMax: Float? = null,
                               var solarRadiationMaxTime: Date? = null,
                               var solarRadiationTotal: Float? = null,

                               var totalPowerProduction: Float? = null,
                               var powerProductionMax: Float? = null,
                               var powerProductionMaxTime: Date? = null,

                               var dailyRain: Float? = null) {

  fun addOutsideTemperature(value: Float?, time: Date) {
    value?.let {
      outsideTemperatureCounter++
      outsideTemperatureAverage = (outsideTemperatureAverage ?: 0.0f) + it
      if (outsideTemperatureMin == null || outsideTemperatureMin!! > it) {
        outsideTemperatureMin = it
        timeOfMinimum = time
      }
      if (outsideTemperatureMax == null || outsideTemperatureMax!! < it) {
        outsideTemperatureMax = it
        timeOfMaximum = time
      }
    }
  }

  fun addOutsideHumidity(value: Float?) {
    value?.let {
      outsideHumidityCounter++
      outsideHumidityAverage = (outsideHumidityAverage ?: 0.0f) + it
      if (outsideHumidityMin == null || outsideHumidityMin!! > it) {
        outsideHumidityMin = it
      }
      if (outsideHumidityMax == null || outsideHumidityMax!! < it) {
        outsideHumidityMax = it
      }
    }
  }

  fun addInsideTemperature(value: Float?) {
    value?.let {
      insideTemperatureCounter++
      insideTemperatureAverage = (insideTemperatureAverage ?: 0.0f) + it
      if (insideTemperatureMin == null || insideTemperatureMin!! > it) {
        insideTemperatureMin = it
      }
      if (insideTemperatureMax == null || insideTemperatureMax!! < it) {
        insideTemperatureMax = it
      }
    }
  }

  fun addInsideHumidity(value: Float?) {
    value?.let {
      insideHumidityCounter++
      insideHumidityAverage = (insideHumidityAverage ?: 0.0f) + it
      if (insideHumidityMin == null || insideHumidityMin!! > it) {
        insideHumidityMin = it
      }
      if (insideHumidityMax == null || insideHumidityMax!! < it) {
        insideHumidityMax = it
      }
    }
  }

  fun addWindspeed(value: Float?) {
    value?.let {
      windspeedCounter++
      windspeedAverage = (windspeedAverage ?: 0.0f) + it
      if (windspeedMin == null || windspeedMin!! > it) {
        windspeedMin = it
      }
    }
  }

  fun updateWindspeedMax(value: Float?) {
    value?.let {
      if (windspeedMax == null || windspeedMax!! < it) {
        windspeedMax = it
      }
    }
  }

  fun normalize() {
    if (outsideTemperatureCounter > 0) {
      outsideTemperatureAverage = outsideTemperatureAverage!! / outsideTemperatureCounter
    }
    if (outsideHumidityCounter > 0) {
      outsideHumidityAverage = outsideHumidityAverage!! / outsideHumidityCounter
    }

    if (insideTemperatureCounter > 0) {
      insideTemperatureAverage = insideTemperatureAverage!! / insideTemperatureCounter
    }
    if (insideHumidityCounter > 0) {
      insideHumidityAverage = insideHumidityAverage!! / insideHumidityCounter
    }
    if (windspeedCounter > 0) {
      windspeedAverage = windspeedAverage!! / windspeedCounter
    }
  }

  fun addSolarRadiation(solarRadiation: Float?) {
    solarRadiation?.let {
      solarRadiationTotal = (solarRadiationTotal ?: 0.0f) + (it / 4) // solarRadiation = durchschnittliche Sonneneinstrahlung
                                                                     // der letzten viertel Stunde
    }
  }

  fun updateSolarRadiationMax(solarRadiation: Float?, timestamp: Date) {
    solarRadiation?.let {
      if (solarRadiationMax == null || solarRadiationMax!! < it) {
        solarRadiationMax = it
        solarRadiationMaxTime = timestamp
      }
      if (it > 0) {
        firstSolarRadiation = firstSolarRadiation ?: timestamp
        lastSolarRadiation = timestamp
      }
    }
  }

  fun updatePowerProductionMax(powerProduction: Float?, timestamp: Date) {
    powerProduction?.let {
      if (powerProductionMax == null || powerProductionMax!! < it) {
        powerProductionMax = powerProduction
        powerProductionMaxTime = timestamp
      }
    }
  }
}

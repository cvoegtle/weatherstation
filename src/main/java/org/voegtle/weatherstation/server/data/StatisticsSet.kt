package org.voegtle.weatherstation.server.data

class StatisticsSet {
  var rain: Float? = null
    internal set
  var maxTemperature: Float? = null
    internal set
  var minTemperature: Float? = null
    internal set

  var solarRadiationMax: Float? = null
    internal set

  fun addRain(rain: Float) {
    this.rain = (this.rain ?: 0.0f) + rain
  }

  fun updateSolarRadiation(solarRadiation: Float) {
    if (solarRadiationMax == null || solarRadiationMax!! < solarRadiation) {
      solarRadiationMax = solarRadiation
    }
  }

  fun setTemperature(temperature: Float?) {
    temperature?.let {
      if (maxTemperature == null || maxTemperature!! < it) {
        maxTemperature = it
      }
      if (minTemperature == null || minTemperature!! > it) {
        minTemperature = it
      }
    }
  }

}

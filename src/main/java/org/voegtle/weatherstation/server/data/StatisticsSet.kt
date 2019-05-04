package org.voegtle.weatherstation.server.data

class StatisticsSet {
  var rain: Float? = null
    internal set
  var maxTemperature: Float? = null
    internal set
  var minTemperature: Float? = null
    internal set

  fun addRain(rain: Float?) {
    rain?.let {
      this.rain = (this.rain ?: 0.0f) + it
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

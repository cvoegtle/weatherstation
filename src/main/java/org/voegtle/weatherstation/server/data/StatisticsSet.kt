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

  var kwh: Float? = null
    internal set


  fun addRain(rain: Float?) {
    if (rain == null) {
      return
    }

    if (this.rain == null) {
      this.rain = rain
    } else {
      this.rain = this.rain!! + rain
    }
  }

  fun setTemperature(temperature: Float?) {
    if (temperature == null) {
      return
    }

    if (maxTemperature == null || maxTemperature!!.compareTo(temperature) < 0) {
      maxTemperature = temperature
    }
    if (minTemperature == null || minTemperature!!.compareTo(temperature) > 0) {
      minTemperature = temperature
    }
  }

  fun addKwh(kwh: Float?) {
    if (kwh == null) {
      return
    }

    if (this.kwh == null) {
      this.kwh = kwh
    } else {
      this.kwh = this.kwh!! + kwh
    }
  }

  fun updateSolarRadiation(solarRadiation: Float) {
    if (solarRadiationMax == null || solarRadiationMax!! < solarRadiation) {
      solarRadiationMax = solarRadiation
    }
  }

}

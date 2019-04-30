package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Ignore
import com.googlecode.objectify.annotation.Index
import org.voegtle.weatherstation.server.weewx.WeewxDataSet

import java.util.Date

@Entity class SmoothedWeatherDataSet(@Id var id: Long? = null,
                                     @Index var timestamp: Date = Date(),
                                     var outsideTemperature: Float? = null,
                                     @Ignore private var countOutsideTemperature: Int = 0,

                                     var outsideHumidity: Float? = null,
                                     @Ignore private var countOutsideHumidity: Int = 0,

                                     var insideTemperature: Float? = null,
                                     @Ignore private var countInsideTemperature: Int = 0,

                                     var insideHumidity: Float? = null,
                                     @Ignore private var countInsideHumidity: Int = 0,

                                     @Index var rainCounter: Int = 0,
                                     var dailyRain: Float = 0.0f,
                                     var isRaining: Boolean? = null,

                                     var windspeed: Float? = null,
                                     var windspeedMax: Float? = null,
                                     @Ignore private var countWindspeed: Int = 0,

                                     var watt: Float? = null,
                                     var kwh: Double? = null,
                                     @Ignore private var countWatt: Int = 0,

                                     var repaired: Boolean? = null) {


  val isValid: Boolean
    get() = outsideTemperature != null && outsideHumidity != null

  fun add(wds: WeewxDataSet) {
    addOutsideTemperature(wds.temperature)
    addOutsideHumidity(wds.humidity)
    addInsideTemperature(wds.indoorTemperature)
    addInsideHumidity(wds.indoorHumidity)
    addDailyRain(wds.dailyRain)
    addWindspeed(wds.windSpeed)
    setWindspeedMaxIfMax(wds.windGust)
  }

  fun normalize() {
    if (countOutsideTemperature > 1) {
      outsideTemperature = outsideTemperature!! / countOutsideTemperature
    }
    if (countOutsideHumidity > 1) {
      outsideHumidity = outsideHumidity!! / countOutsideHumidity
    }
    if (countInsideTemperature > 1) {
      insideTemperature = insideTemperature!! / countInsideTemperature
    }
    if (countInsideHumidity > 1) {
      insideHumidity = insideHumidity!! / countInsideHumidity
    }
    if (countWindspeed > 1) {
      windspeed = windspeed!! / countWindspeed
    }
    if (countWatt > 0) {
      watt = watt!! / countWatt
    }
  }

  private fun addOutsideTemperature(value: Float?) {
    value?.let {
      countOutsideTemperature++
      var newTemp = outsideTemperature ?: 0.0f
      newTemp += it
      outsideTemperature = newTemp
    }
  }

  private fun addOutsideHumidity(value: Float?) {
    value?.let {
      countOutsideHumidity++
      var newHumidity = outsideHumidity ?: 0.0f
      newHumidity += it
      outsideHumidity = newHumidity
    }
  }

  private fun addInsideTemperature(value: Float?) {
    value?.let {
      countInsideTemperature++
      var newTemp = insideTemperature ?: 0.0f
      newTemp += it
      insideTemperature = newTemp
    }
  }

  private fun addInsideHumidity(value: Float?) {
    value?.let {
      countInsideHumidity++
      var newHumidity = insideHumidity ?: 0.0f
      newHumidity += it
      insideHumidity = newHumidity
    }
  }

  private fun addRainCount(rainCounter: Int?) {
    if (rainCounter != null) {
      if (this.rainCounter == null) {
        this.rainCounter = rainCounter
      } else if (rainCounter > this.rainCounter) {
        this.rainCounter = rainCounter
      }
    }
  }

  private fun addDailyRain(dailyRain: Float) {
    if (this.dailyRain < dailyRain) {
      this.dailyRain = dailyRain
    }
  }

  private fun addRaining(raining: Boolean?) {
    if (raining != null) {
      if (isRaining == null) {
        isRaining = raining
      } else if (raining) {
        isRaining = true
      }
    }
  }

  private fun addWindspeed(value: Float?) {
    value?.let {
      countWindspeed++
      var newSpeed = windspeed ?: 0.0f
      newSpeed += it
      windspeed = newSpeed
    }
  }

  private fun setWindspeedMaxIfMax(value: Float?) {
    value?.let {
      if (windspeedMax == null || windspeedMax!! < it) {
        windspeedMax = it
      }
    }
  }

  private fun addWatt(value: Float?) {
    value?.let {
      countWatt++
      var newWatt = watt ?: 0.0f
      newWatt += it
      watt = newWatt
    }
  }

  private fun addKwh(value: Double?) {
    value?.let {
      if (kwh == null || value > kwh!!) {
        kwh = value
      }
    }
  }

  companion object {

    fun hasRainCounter(sds: SmoothedWeatherDataSet?): Boolean {
      return sds != null && sds.rainCounter != null
    }
  }
}

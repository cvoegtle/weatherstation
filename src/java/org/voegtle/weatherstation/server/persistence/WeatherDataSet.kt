package org.voegtle.weatherstation.server.persistence

import com.google.appengine.api.datastore.Key
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class WeatherDataSet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val key: Key? = null

  var timestamp: Date? = null
  var outsideTemperature: Float? = null
  var outsideHumidity: Float? = null
  var insideTemperature: Float? = null
  var insideHumidity: Float? = null
  var rainCounter: Int? = null
  var isRaining: Boolean? = null

  var windspeed: Float? = null
  var watt: Float? = null
  var kwh: Double? = null

  val isValid: Boolean
    get() = outsideTemperature != null && outsideHumidity != null

  constructor()

  constructor(timestamp: Date) {
    this.timestamp = timestamp
  }

  override fun toString(): String {
    return "WeatherDataSet={ TS=$timestamp, OutT=$outsideTemperature, OutH=$outsideHumidity, valid=$isValid}"
  }

  companion object {

    fun hasRainCounter(wds: WeatherDataSet?): Boolean {
      return wds != null && wds.rainCounter != null
    }
  }
}

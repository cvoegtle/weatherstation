package org.voegtle.weatherstation.server.data

import java.io.Serializable
import java.util.Date

class UnformattedWeatherDTO : Serializable {
  var time: Date? = null
  var localTime: String? = null
  var temperature: Float? = null
  var insideTemperature: Float? = null
  var humidity: Float? = null
  var isRaining: Boolean? = null
  var rainLastHour: Float? = null
  var rainToday: Float? = null
  var windspeed: Float? = null
  var insideHumidity: Float? = null
  var watt: Float? = null

}

package org.voegtle.weatherstation.server.data

import java.io.Serializable
import java.util.*

class UnformattedWeatherDTO(
  val time: Date,
  val location: String,
  val localtime: String,
  val temperature: Float,
  val insideTemperature: Float? = null,
  val humidity: Float?,
  val barometer: Float?,
  val isRaining: Boolean,
  val rainLastHour: Float = 0.0f,
  val rainToday: Float = 0.0f,
  val windspeed: Float?,
  val windgust: Float?,
  val insideHumidity: Float?,
  val solarradiation: Float? = null,
  val UV: Float? = null,
  val powerProduction: Float? = null,
  val powerFeed: Float? = null
) : Serializable

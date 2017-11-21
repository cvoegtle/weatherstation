package org.voegtle.weatherstation.server.data

import java.io.Serializable
import java.util.Date

class UnformattedWeatherDTO(val time: Date,
                            val localTime: String,
                            val temperature: Float,
                            val insideTemperature: Float? = null,
                            val humidity: Float?,
                            val isRaining: Boolean,
                            val rainLastHour: Float?,
                            val rainToday: Float?,
                            val windspeed: Float?,
                            val insideHumidity: Float?,
                            val watt: Float?) : Serializable

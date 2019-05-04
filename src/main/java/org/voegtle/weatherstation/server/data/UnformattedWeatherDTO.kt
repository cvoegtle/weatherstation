package org.voegtle.weatherstation.server.data

import java.io.Serializable
import java.util.Date

class UnformattedWeatherDTO(val time: Date,
                            val location: String,
                            val localtime: String,
                            val temperature: Float,
                            val insideTemperature: Float? = null,
                            val humidity: Float?,
                            val barometer: Float?,
                            val isRaining: Boolean,
                            val rainLastHour: Float?,
                            val rainToday: Float?,
                            val windspeed: Float?,
                            val insideHumidity: Float?,
                            val solarradiation: Float? = null,
                            val UV: Float? = null) : Serializable

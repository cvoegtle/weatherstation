package org.voegtle.weatherstation.server.data

import java.util.*

data class UnformattedWeatherDTO(
    val time: Date,
    val localTime: String,
    val temperature: Float,
    val insideTemperature: Float? = null,
    val humidity: Float,
    val isRaining: Boolean? = null,
    val rainLastHour: Float? = null,
    val rainToday: Float? = null,
    val windspeed: Float? = null,
    val insideHumidity: Float? = null,
    val watt: Float? = null)

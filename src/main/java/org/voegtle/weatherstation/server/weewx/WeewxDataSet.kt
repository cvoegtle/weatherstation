package org.voegtle.weatherstation.server.weewx

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import java.util.Date

@Entity data class WeewxDataSet(@Id val time: Date,
                                val temperature: Float,
                                val humidity: Int,
                                val barometer: Float,
                                val dailyRain: Float,
                                val rain: Float,
                                val UV: Float?,
                                val solarRadiation: Float?,
                                val windDirection: Int?,
                                val windSpeed: Float?,
                                val windGust: Float?,
                                val indoorTemperature: Float?,
                                val indoorHumidity: Float?)

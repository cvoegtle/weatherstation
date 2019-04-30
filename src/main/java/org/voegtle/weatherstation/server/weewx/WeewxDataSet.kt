package org.voegtle.weatherstation.server.weewx

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import java.util.Date

@Entity data class WeewxDataSet(@Id var id: Long? = null,
                                @Index var time: Date = Date(),
                                var temperature: Float = 0.0f,
                                var humidity: Float = 0.0f,
                                var barometer: Float = 0.0f,
                                var dailyRain: Float = 0.0f,
                                var rain: Float = 0.0f,
                                var UV: Float? = null,
                                var solarRadiation: Float? = null,
                                var windDirection: Int? = null,
                                var windSpeed: Float? = null,
                                var windGust: Float? = null,
                                var indoorTemperature: Float? = null,
                                var indoorHumidity: Float? = null)

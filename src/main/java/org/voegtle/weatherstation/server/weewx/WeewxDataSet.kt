package org.voegtle.weatherstation.server.weewx

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import java.util.Date

@Entity data class WeewxDataSet(@Id var id: Long? = null,
                                @Index var time: Date,
                                var temperature: Float,
                                var humidity: Float,
                                var barometer: Float,
                                var dailyRain: Float,
                                var rain: Float,
                                var UV: Float?,
                                var solarRadiation: Float?,
                                var windDirection: Int?,
                                var windSpeed: Float?,
                                var windGust: Float?,
                                var indoorTemperature: Float?,
                                var indoorHumidity: Float?)

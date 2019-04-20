package org.voegtle.weatherstation.server.rapid

import java.util.Date

data class RapidDataSet(val time: Date, val temperature: Float, val humidity: Int, val barometer: Float, val dailyRain: Float, val rain: Float,
                        val UV: Float?, val solarRadiation: Float?, val windDirection: Int?, val windSpeed: Float?, val windGust: Float?,
                        val indoorTemperature: Float?, val indoorHumidity: Float?)
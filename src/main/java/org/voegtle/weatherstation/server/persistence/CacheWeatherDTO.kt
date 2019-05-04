package org.voegtle.weatherstation.server.persistence

import java.io.Serializable
import java.util.Date

class CacheWeatherDTO (  var id: String,
                         var time: Date,
                         var localTime: String,
                         var location: String,
                         var locationShort: String,
                         var temperature: Float,
                         var humidity: Float? = null,
                         var solarRadiation: Float? = null,
                         var UV: Float? = null,
                         var latitude: Float,
                         var longitude: Float,
                         var insideTemperature: Float? = null,
                         var insideHumidity: Float? = null,
                         var isRaining: Boolean = false,
                         var rainLastHour: Float? = null,
                         var rainToday: Float? = null,
                         var windspeed: Float? = null,
                         var watt: Float? = null,
                         var forecast: String? = null
                         ) : Serializable

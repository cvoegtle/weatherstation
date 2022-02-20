package org.voegtle.weatherstation.server.persistence

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CacheWeatherDTO(var id: String,
                           var time: Date,
                           var localTime: String,
                           var location: String,
                           var locationShort: String,
                           var temperature: Float,
                           var humidity: Float? = null,
                           var barometer: Float? = null,
                           var solarradiation: Float? = null,
                           var UV: Float? = null,
                           var powerProduction: Float? = null,
                           var powerFeed: Float? = null,
                           var latitude: Float,
                           var longitude: Float,
                           var insideTemperature: Float? = null,
                           var insideHumidity: Float? = null,
                           var raining: Boolean = false,
                           var rainLastHour: Float? = null,
                           var rainToday: Float? = null,
                           var windspeed: Float? = null,
                           var windgust: Float? = null,
                           var forecast: String? = null)

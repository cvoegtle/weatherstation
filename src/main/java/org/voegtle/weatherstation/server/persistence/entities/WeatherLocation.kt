package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id

@Entity
data class WeatherLocation(
    @Id
    var id: Long? = null,
    var location: String,
    var host: String,
    var isForwardSecret:Boolean = false,
    var readHash: String? = null
)

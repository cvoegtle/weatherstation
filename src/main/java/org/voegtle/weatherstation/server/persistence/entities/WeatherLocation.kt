package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id

@Entity
class WeatherLocation {
    @Id
    var id: Long? = null
    var location: String? = null
    var host: String? = null
    var isForwardSecret = false
    var readHash: String? = null
}

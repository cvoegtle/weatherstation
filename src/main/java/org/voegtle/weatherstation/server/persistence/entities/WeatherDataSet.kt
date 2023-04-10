package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import java.util.*

@Entity
class WeatherDataSet {
    @Id
    var id: Long? = null
    var timestamp: Date = Date()
    var outsideTemperature: Float? = null
    var outsideHumidity: Float? = null
    var insideTemperature: Float? = null
    var insideHumidity: Float? = null
    var rainCounter: Int? = null
    var isRaining: Boolean? = null
    var windspeed: Float? = null
    var watt: Float? = null
    var kwh: Double? = null

    constructor()
    constructor(timestamp: Date) {
        this.timestamp = timestamp
    }

    val isValid: Boolean
        get() = outsideTemperature != null && outsideHumidity != null

    override fun toString(): String {
        return "WeatherDataSet={ TS=$timestamp, OutT=$outsideTemperature, OutH=$outsideHumidity, valid=$isValid}"
    }

    companion object {
        fun hasRainCounter(wds: WeatherDataSet?): Boolean {
            return wds != null && wds.rainCounter != null
        }
    }
}

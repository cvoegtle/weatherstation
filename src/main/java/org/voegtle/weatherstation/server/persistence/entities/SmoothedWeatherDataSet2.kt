package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import java.util.*

@Entity
class SmoothedWeatherDataSet2 {
    @Id
    var id: Long? = null
    @Index
    var timestamp: Date
    var outsideTemperature: Float? = null
    var outsideHumidity: Float? = null
    var insideTemperature: Float? = null
    var insideHumidity: Float? = null
    var rainCounter: Int? = null
    var dailyRain: Float? = null
    var isRaining: Boolean? = null
    var windspeed: Float? = null
    var windspeedMax: Float? = null
    var watt: Float? = null
    var kwh: Double? = null
    var repaired: Boolean? = null

    @Transient
    private var countOutsideTemperature = 0

    @Transient
    private var countOutsideHumidity = 0

    @Transient
    private var countInsideTemperature = 0

    @Transient
    private var countInsideHumidity = 0

    @Transient
    private var countWindspeed = 0

    @Transient
    private var countWatt = 0

    constructor() {
        timestamp = Date()
    }

    constructor(timestamp: Date) {
        this.timestamp = timestamp
    }

    fun add(wds: WeatherDataSet) {
        addOutsideTemperature(wds.outsideTemperature)
        addOutsideHumidity(wds.outsideHumidity)
        addInsideTemperature(wds.insideTemperature)
        addInsideHumidity(wds.insideHumidity)
        addRainCount(wds.rainCounter)
        addRaining(wds.isRaining)
        addWindspeed(wds.windspeed)
        setWindspeedMaxIfMax(wds.windspeed)
        addWatt(wds.watt)
        addKwh(wds.kwh)
    }

    fun normalize() {
        if (countOutsideTemperature > 1) {
            outsideTemperature = outsideTemperature!! / countOutsideTemperature
        }
        if (countOutsideHumidity > 1) {
            outsideHumidity = outsideHumidity!! / countOutsideHumidity
        }
        if (countInsideTemperature > 1) {
            insideTemperature = insideTemperature!! / countInsideTemperature
        }
        if (countInsideHumidity > 1) {
            insideHumidity = insideHumidity!! / countInsideHumidity
        }
        if (countWindspeed > 1) {
            windspeed = windspeed!! / countWindspeed
        }
        if (countWatt > 0) {
            watt = watt!! / countWatt
        }
    }

    private fun addOutsideTemperature(value: Float?) {
        var value = value
        if (value != null) {
            countOutsideTemperature++
            if (outsideTemperature != null) {
                value = value + outsideTemperature!!
            }
            outsideTemperature = value
        }
    }

    private fun addOutsideHumidity(value: Float?) {
        var value = value
        if (value != null) {
            countOutsideHumidity++
            if (outsideHumidity != null) {
                value = value + outsideHumidity!!
            }
            outsideHumidity = value
        }
    }

    private fun addInsideTemperature(value: Float?) {
        var value = value
        if (value != null) {
            countInsideTemperature++
            if (insideTemperature != null) {
                value = value + insideTemperature!!
            }
            insideTemperature = value
        }
    }

    private fun addInsideHumidity(value: Float?) {
        var value = value
        if (value != null) {
            countInsideHumidity++
            if (insideHumidity != null) {
                value = value + insideHumidity!!
            }
            insideHumidity = value
        }
    }

    private fun addRainCount(rainCounter: Int?) {
        if (rainCounter != null) {
            if (this.rainCounter == null) {
                this.rainCounter = rainCounter
            } else if (rainCounter > this.rainCounter!!) {
                this.rainCounter = rainCounter
            }
        }
    }

    private fun addRaining(raining: Boolean?) {
        if (raining != null) {
            if (isRaining == null) {
                isRaining = raining
            } else if (raining) {
                isRaining = true
            }
        }
    }

    private fun addWindspeed(value: Float?) {
        var value = value
        if (value != null) {
            countWindspeed++
            if (windspeed != null) {
                value = value + windspeed!!
            }
            windspeed = value
        }
    }

    private fun setWindspeedMaxIfMax(value: Float?) {
        if (value != null) {
            if (windspeedMax == null || windspeedMax!!.compareTo(value) < 0) {
                windspeedMax = value
            }
        }
    }

    private fun addWatt(value: Float?) {
        var value = value
        if (value != null) {
            countWatt++
            if (watt != null) {
                value = value + watt!!
            }
            watt = value
        }
    }

    private fun addKwh(value: Double?) {
        if (value != null) {
            if (kwh == null || value > kwh!!) {
                kwh = value
            }
        }
    }

    val isValid: Boolean
        get() = outsideTemperature != null && outsideHumidity != null

    override fun toString(): String {
        return "SmoothedWeatherDataSet2{" +
                "timestamp=" + timestamp +
                ", rainCounter=" + rainCounter +
                ", dailyRain=" + dailyRain +
                "}"
    }

    companion object {
        fun hasRainCounter(sds: SmoothedWeatherDataSet2?): Boolean {
            return sds != null && sds.rainCounter != null
        }
    }
}

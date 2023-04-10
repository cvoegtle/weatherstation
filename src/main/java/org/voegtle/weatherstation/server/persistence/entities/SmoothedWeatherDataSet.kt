package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Ignore
import java.util.*

@Entity
class SmoothedWeatherDataSet {
    @Id
    var id: Long? = null
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

    @Ignore
    private var countOutsideTemperature = 0

    @Ignore
    private var countOutsideHumidity = 0

    @Ignore
    private var countInsideTemperature = 0

    @Ignore
    private var countInsideHumidity = 0

    @Ignore
    private var countWindspeed = 0

    @Ignore
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
        value?.let {
            countOutsideTemperature++
            outsideTemperature = (outsideTemperature ?: 0.0f) + it
        }
    }

    private fun addOutsideHumidity(value: Float?) {
        value?.let {
            countOutsideHumidity++
            outsideHumidity = (outsideHumidity ?: 0.0f) + it
        }
    }

    private fun addInsideTemperature(value: Float?) {
        value?.let {
            countInsideTemperature++
            insideTemperature = (insideTemperature ?: 0.0f) + it
        }
    }

    private fun addInsideHumidity(value: Float?) {
        value?.let {
            countInsideHumidity++
            insideHumidity = (insideHumidity ?: 0.0f) + value
        }
    }

    private fun addRainCount(rainCounter: Int?) {
        rainCounter?.let {
            if (this.rainCounter == null) {
                this.rainCounter = it
            } else if (it > this.rainCounter!!) {
                this.rainCounter = rainCounter
            }
        }
    }

    private fun addRaining(raining: Boolean?) {
        raining?.let {
            if (isRaining == null) {
                isRaining = it
            } else if (it) {
                isRaining = true
            }
        }
    }

    private fun addWindspeed(value: Float?) {
        value?.let {
            countWindspeed++
            windspeed = (windspeed ?: 0.0f) + it
        }
    }

    private fun setWindspeedMaxIfMax(value: Float?) {
        value?.let {
            if (windspeedMax == null || windspeedMax!!.compareTo(it) < 0) {
                windspeedMax = it
            }
        }
    }

    private fun addWatt(value: Float?) {
        value?.let {
            countWatt++
            watt = (watt ?: 0.0f) + it
        }
    }

    private fun addKwh(value: Double?) {
        value?.let {
            if (kwh == null || it > kwh!!) {
                kwh = value
            }
        }
    }

    val isValid: Boolean
        get() = outsideTemperature != null && outsideHumidity != null

    override fun toString(): String {
        return "SmoothedWeatherDataSet{" +
                "timestamp=" + timestamp +
                ", rainCounter=" + rainCounter +
                ", dailyRain=" + dailyRain +
                "}"
    }

    companion object {
        fun hasRainCounter(sds: SmoothedWeatherDataSet?): Boolean {
            return sds != null && sds.rainCounter != null
        }
    }
}

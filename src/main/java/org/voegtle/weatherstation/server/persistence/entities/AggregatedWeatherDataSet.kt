package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Ignore
import com.googlecode.objectify.annotation.Index
import org.voegtle.weatherstation.server.persistence.PeriodEnum
import java.util.*

@Entity
class AggregatedWeatherDataSet {
    @Id
    var id: Long? = null

    @Index
    var date: Date = Date()
        private set
    var period: PeriodEnum? = null
    var isFinished = false
    var timeOfMinimum: Date? = null
        private set
    var timeOfMaximum: Date? = null
        private set
    var outsideTemperatureMin: Float? = null
        private set
    var outsideTemperatureMax: Float? = null
        private set
    var outsideTemperatureAverage: Float? = null
        private set

    @Ignore
    private var outsideTemperatureCounter = 0
    var outsideHumidityMin: Float? = null
        private set
    var outsideHumidityMax: Float? = null
        private set
    var outsideHumidityAverage: Float? = null
        private set

    @Ignore
    private var outsideHumidityCounter = 0
    var insideTemperatureMin: Float? = null
        private set
    var insideTemperatureMax: Float? = null
        private set
    var insideTemperatureAverage: Float? = null
        private set

    @Ignore
    private var insideTemperatureCounter = 0
    var insideHumidityMin: Float? = null
        private set
    var insideHumidityMax: Float? = null
        private set
    var insideHumidityAverage: Float? = null
        private set

    @Ignore
    private var insideHumidityCounter = 0
    var windspeedMin: Float? = null
    var windspeedMax: Float? = null
    var windspeedAverage: Float? = null

    @Ignore
    private var windspeedCounter = 0

    var totalPowerProduction: Float? = null
    var powerProductionMax: Float? = null
    var powerProductionMaxTime: Date? = null

    var rainCounter = 0
    var rainDays = 0
    var dailyRain: Float? = null
    var kwh: Double? = null

    constructor()
    constructor(date: Date) {
        this.date = date
        this.period = PeriodEnum.DAY
    }

    fun addOutsideTemperature(value: Float?, time: Date?) {
        if (value == null) {
            return
        }
        if (outsideTemperatureAverage == null) {
            outsideTemperatureAverage = 0.0.toFloat()
        }
        outsideTemperatureCounter++
        outsideTemperatureAverage = outsideTemperatureAverage!! + value
        if (outsideTemperatureMin == null || outsideTemperatureMin!! > value) {
            outsideTemperatureMin = value
            timeOfMinimum = time
        }
        if (outsideTemperatureMax == null || outsideTemperatureMax!! < value) {
            outsideTemperatureMax = value
            timeOfMaximum = time
        }
    }

    fun addOutsideHumidity(value: Float?) {
        if (value == null) {
            return
        }
        if (outsideHumidityAverage == null) {
            outsideHumidityAverage = 0.0.toFloat()
        }
        outsideHumidityCounter++
        outsideHumidityAverage = outsideHumidityAverage!! + value
        if (outsideHumidityMin == null || outsideHumidityMin!! > value) {
            outsideHumidityMin = value
        }
        if (outsideHumidityMax == null || outsideHumidityMax!! < value) {
            outsideHumidityMax = value
        }
    }

    fun addInsideTemperature(value: Float?) {
        if (value == null) {
            return
        }
        if (insideTemperatureAverage == null) {
            insideTemperatureAverage = 0.0.toFloat()
        }
        insideTemperatureCounter++
        insideTemperatureAverage = insideTemperatureAverage!! + value
        if (insideTemperatureMin == null || insideTemperatureMin!! > value) {
            insideTemperatureMin = value
        }
        if (insideTemperatureMax == null || insideTemperatureMax!! < value) {
            insideTemperatureMax = value
        }
    }

    fun addInsideHumidity(value: Float?) {
        if (value == null) {
            return
        }
        if (insideHumidityAverage == null) {
            insideHumidityAverage = 0.0.toFloat()
        }
        insideHumidityCounter++
        insideHumidityAverage = insideHumidityAverage!! + value
        if (insideHumidityMin == null || insideHumidityMin!! > value) {
            insideHumidityMin = value
        }
        if (insideHumidityMax == null || insideHumidityMax!! < value) {
            insideHumidityMax = value
        }
    }

    fun addWindspeed(value: Float?) {
        if (value == null) {
            return
        }
        if (windspeedAverage == null) {
            windspeedAverage = 0.0.toFloat()
        }
        windspeedCounter++
        windspeedAverage = windspeedAverage!! + value
        if (windspeedMin == null || windspeedMin!! > value) {
            windspeedMin = value
        }
        if (windspeedMax == null || windspeedMax!! < value) {
            windspeedMax = value
        }
    }

    fun normalize() {
        if (outsideTemperatureCounter > 0) {
            outsideTemperatureAverage = outsideTemperatureAverage!! / outsideTemperatureCounter
        }
        if (outsideHumidityCounter > 0) {
            outsideHumidityAverage = outsideHumidityAverage!! / outsideHumidityCounter
        }
        if (insideTemperatureCounter > 0) {
            insideTemperatureAverage = insideTemperatureAverage!! / insideTemperatureCounter
        }
        if (insideHumidityCounter > 0) {
            insideHumidityAverage = insideHumidityAverage!! / insideHumidityCounter
        }
        if (windspeedCounter > 0) {
            windspeedAverage = windspeedAverage!! / windspeedCounter
        }
    }

    fun updatePowerProductionMax(powerProduction: Float?, timestamp: Date) {
        powerProduction?.let {
            if (powerProductionMax == null || powerProductionMax!! < it) {
                powerProductionMax = powerProduction
                powerProductionMaxTime = timestamp
            }
        }
    }

    override fun toString(): String {
        return "AggregatedWeatherDataSet(id=$id, date=$date, period=$period, isFinished=$isFinished, timeOfMinimum=$timeOfMinimum, timeOfMaximum=$timeOfMaximum, outsideTemperatureMin=$outsideTemperatureMin, outsideTemperatureMax=$outsideTemperatureMax, outsideTemperatureAverage=$outsideTemperatureAverage, outsideTemperatureCounter=$outsideTemperatureCounter, outsideHumidityMin=$outsideHumidityMin, outsideHumidityMax=$outsideHumidityMax, outsideHumidityAverage=$outsideHumidityAverage, outsideHumidityCounter=$outsideHumidityCounter, insideTemperatureMin=$insideTemperatureMin, insideTemperatureMax=$insideTemperatureMax, insideTemperatureAverage=$insideTemperatureAverage, insideTemperatureCounter=$insideTemperatureCounter, insideHumidityMin=$insideHumidityMin, insideHumidityMax=$insideHumidityMax, insideHumidityAverage=$insideHumidityAverage, insideHumidityCounter=$insideHumidityCounter, windspeedMin=$windspeedMin, windspeedMax=$windspeedMax, windspeedAverage=$windspeedAverage, windspeedCounter=$windspeedCounter, totalPowerProduction=$totalPowerProduction, powerProductionMax=$powerProductionMax, powerProductionMaxTime=$powerProductionMaxTime, rainCounter=$rainCounter, rainDays=$rainDays, dailyRain=$dailyRain, kwh=$kwh)"
    }


}

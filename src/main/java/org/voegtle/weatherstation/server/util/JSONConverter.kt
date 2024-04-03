package org.voegtle.weatherstation.server.util

import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.StatisticsSet
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import java.text.SimpleDateFormat
import java.util.*

class JSONConverter(private val locationProperties: LocationProperties) {
    private val FORMAT_DATE = "yyyy-MM-dd"

    fun toJson(currentWeatherData: UnformattedWeatherDTO): JSONObject {
        val json = WeatherJSONObject()
        json.put("timestamp", currentWeatherData.time)
        json.putOpt("temperature", currentWeatherData.temperature)
        json.putOpt("inside_temperature", currentWeatherData.insideTemperature)
        json.putOpt("humidity", currentWeatherData.humidity)
        json.putOpt("inside_humidity", currentWeatherData.insideHumidity)
        json.putOpt("rain", currentWeatherData.rainLastHour)
        json.putOpt("rain_today", currentWeatherData.rainToday)

        json.putOpt("raining", currentWeatherData.isRaining)
        json.putOpt("wind", multiply(currentWeatherData.windspeed, locationProperties.windMultiplier))

        json.putOpt("powerProduction", currentWeatherData.powerProduction)
        json.putOpt("powerFeed", currentWeatherData.powerFeed)

        json.put("location", locationProperties.city)
        json.put("location_short", locationProperties.cityShortcut)
        json.putOpt("localtime", currentWeatherData.localTime)

        json.put("id", locationProperties.location)
        json.putOpt("forecast", locationProperties.weatherForecast)

        json.putOpt("latitude", locationProperties.latitude)
        json.putOpt("longitude", locationProperties.longitude)

        return json
    }

    fun toJson(rain: RainDTO): JSONObject {
        val json = WeatherJSONObject()
        json.put("lastHour", rain.lastHour)
        json.put("today", rain.today)
        json.put("yesterday", rain.yesterday)
        json.put("lastWeek", rain.lastWeek)
        json.put("last30days", rain.last30Days)
        return json
    }

    fun toJson(list: List<SmoothedWeatherDataSet>, extended: Boolean): ArrayList<JSONObject> {
        val dateUtil = locationProperties.dateUtil

        var previousRainCounter: Int? = null
        val jsonObjects = ArrayList<JSONObject>()
        for (wds in list) {
            val json = WeatherJSONObject()
            json.put("timestamp", dateUtil.toLocalDate(wds.timestamp))
            json.put("temperature", wds.outsideTemperature)
            if (wds.insideTemperature != null && extended) {
                json.put("inside_temperature", wds.insideTemperature)
            }

            json.put("humidity", wds.outsideHumidity)
            if (wds.insideHumidity != null && extended) {
                json.put("inside_humidity", wds.insideHumidity)
            }

            if (previousRainCounter != null && wds.rainCounter != null) {
                val rain = 0.295 * (wds.rainCounter!! - previousRainCounter)
                json.put("rain", Math.max(rain, 0.0))
            } else {
                json.put("rain", 0.0)
            }
            previousRainCounter = wds.rainCounter
            json.put("wind", multiply(wds.windspeed, locationProperties.windMultiplier))
            json.put("windMax", multiply(wds.windspeedMax, locationProperties.windMultiplier))

            json.put("powerFeed", wds.powerFeed)
            json.put("powerProduction", wds.powerProduction)

            jsonObjects.add(json)
        }

        return jsonObjects
    }

    private fun multiply(input: Float?, factor: Float?): Float? {
        var number = input
        number?.let {
            number *= factor!!
        }
        return number
    }


    fun toJsonAggregated(list: List<AggregatedWeatherDataSet>): ArrayList<JSONObject> {
        val sdf = SimpleDateFormat(FORMAT_DATE)

        val jsonObjects = ArrayList<JSONObject>()
        for (wds in list) {
            val json = WeatherJSONObject()
            json.put("date", sdf.format(wds.date))
            json.put("tempAvg", wds.outsideTemperatureAverage)
            json.put("tempMin", wds.outsideTemperatureMin)
            json.put("tempMax", wds.outsideTemperatureMax)
            json.put("humAvg", wds.outsideHumidityAverage)
            json.put("humMin", wds.outsideHumidityMin)
            json.put("humMax", wds.outsideHumidityMax)
            json.put("wind", multiply(wds.windspeedAverage, locationProperties.windMultiplier))
            json.put("windMax", multiply(wds.windspeedMax, locationProperties.windMultiplier))
            val rain = 0.295 * wds.rainCounter
            json.put("rain", Math.max(rain, 0.0))
            json.put("totalPowerProduction", wds.totalPowerProduction)
            json.put("powerProductionMax", wds.powerProductionMax)

            jsonObjects.add(json)
        }
        return jsonObjects
    }

    fun toJson(stats: Statistics): JSONObject {
        val json = WeatherJSONObject()
        json.put("id", locationProperties.location)
        json.put("kind", stats.kind)

        val jsonObjects = ArrayList<JSONObject>()
        if (stats.rainLastHour != null) {
            val lastHour = StatisticsSet()
            lastHour.addRain(stats.rainLastHour)
            jsonObjects.add(toJson(Statistics.TimeRange.lastHour, lastHour))
        }

        jsonObjects.add(toJson(Statistics.TimeRange.today, stats.today))
        jsonObjects.add(toJson(Statistics.TimeRange.yesterday, stats.yesterday))
        jsonObjects.add(toJson(Statistics.TimeRange.last7days, stats.last7days))
        jsonObjects.add(toJson(Statistics.TimeRange.last30days, stats.last30days))
        json.put("stats", jsonObjects)

        return json
    }

    @Throws(JSONException::class)
    private fun toJson(range: Statistics.TimeRange, set: StatisticsSet): JSONObject {
        val json = WeatherJSONObject()
        json.put("range", range)
        json.putOpt("rain", set.rain)
        json.putOpt("minTemperature", set.minTemperature)
        json.putOpt("maxTemperature", set.maxTemperature)
        json.putOpt("solarRadiationMax", set.solarRadiationMax)
        json.putOpt("kwh", set.kwh)
        return json
    }
}

private fun JSONObject.getFloat(key: String): Float = (this.get(key) as Number).toFloat()

private fun JSONObject.optFloat(key: String): Float? = (this.opt(key) as Number?)?.toFloat()


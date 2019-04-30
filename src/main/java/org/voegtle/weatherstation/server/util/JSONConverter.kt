package org.voegtle.weatherstation.server.util

import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.StatisticsSet
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import java.util.ArrayList
import java.util.Date

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
    json.putOpt("solarradidation", currentWeatherData.solarradiation)

    json.put("location", currentWeatherData.location)
    json.put("location_short", locationProperties.cityShortcut)
    json.putOpt("localtime", currentWeatherData.localtime)

    json.put("id", locationProperties.location)
    json.putOpt("forecast", locationProperties.weatherForecast)

    json.putOpt("latitude", locationProperties.latitude)
    json.putOpt("longitude", locationProperties.longitude)

    return json
  }

  fun toJson(currentWeatherData: CacheWeatherDTO): JSONObject {
    val json = WeatherJSONObject()
    json.put("timestamp", currentWeatherData.time)
    json.putOpt("temperature", currentWeatherData.temperature)
    json.putOpt("inside_temperature", currentWeatherData.insideTemperature)
    json.putOpt("humidity", currentWeatherData.humidity)
    json.putOpt("inside_humidity", currentWeatherData.insideHumidity)
    json.putOpt("rain", currentWeatherData.rainLastHour)
    json.putOpt("rain_today", currentWeatherData.rainToday)

    json.putOpt("raining", currentWeatherData.isRaining)
    json.putOpt("wind", currentWeatherData.windspeed)
    json.putOpt("watt", currentWeatherData.watt)

    json.put("location", currentWeatherData.location)
    json.put("location_short", currentWeatherData.locationShort)
    json.putOpt("localtime", currentWeatherData.localTime)

    json.put("id", currentWeatherData.id)
    json.putOpt("forecast", currentWeatherData.forecast)

    json.putOpt("latitude", currentWeatherData.latitude)
    json.putOpt("longitude", currentWeatherData.longitude)

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

  private fun multiply(input: Float?, factor: Float?): Float? {
    var number = input
    number?.let {
      number *= factor!!
    }
    return number
  }


  fun toJson(stats: Statistics, newFormat: Boolean): JSONObject {
    val json = WeatherJSONObject()
    json.put("id", locationProperties.location)

    val jsonObjects = ArrayList<JSONObject>()
    if (stats.rainLastHour != null) {
      val lastHour = StatisticsSet()
      lastHour.addRain(stats.rainLastHour)
      jsonObjects.add(toJson(Statistics.TimeRange.lastHour, lastHour, newFormat))
    }

    jsonObjects.add(toJson(Statistics.TimeRange.today, stats.today, newFormat))
    jsonObjects.add(toJson(Statistics.TimeRange.yesterday, stats.yesterday, newFormat))
    jsonObjects.add(toJson(Statistics.TimeRange.last7days, stats.last7days, newFormat))
    jsonObjects.add(toJson(Statistics.TimeRange.last30days, stats.last30days, newFormat))
    json.put("stats", jsonObjects)

    return json
  }

  @Throws(JSONException::class)
  private fun toJson(range: Statistics.TimeRange, set: StatisticsSet, newFormat: Boolean): JSONObject {
    val json = WeatherJSONObject()
    json.put("range", range)
    if (newFormat) {
      json.putOpt("rain", set.rain)
      json.putOpt("minTemperature", set.minTemperature)
      json.putOpt("maxTemperature", set.maxTemperature)
    } else {
      json.put("rain", set.rain)
      json.put("minTemperature", set.minTemperature)
      json.put("maxTemperature", set.maxTemperature)
    }
    json.putOpt("kwh", set.kwh)
    return json
  }

  @Throws(JSONException::class)
  fun decodeWeatherDTO(encodedWeatherData: String): CacheWeatherDTO {
    val json = JSONObject(encodedWeatherData)
    val timestamp = json.getString("timestamp")
    return CacheWeatherDTO(id = json.getString("id"),
                           time = Date(timestamp),
                           localTime = json.getString("localtime"),

                           location = json.getString("location"),
                           locationShort = json.getString("location_short"),
                           forecast = json.getString("forecast"),

                           temperature = json.getFloat("temperature"),
                           humidity = json.optFloat("humidity"),
                           insideTemperature = json.optFloat("inside_temperature"),
                           insideHumidity = json.optFloat("inside_humidity"),
                           watt = json.optFloat("watt"),
                           rainLastHour = json.optFloat("rain"),
                           rainToday = json.optFloat("rain_today"),
                           isRaining = json.optBoolean("raining"),
                           windspeed = json.optFloat("wind"),
                           latitude = json.getFloat("latitude"),
                           longitude = json.getFloat("longitude"))
  }
}

private fun JSONObject.getFloat(key: String): Float = (this.get(key) as Number).toFloat()

private fun JSONObject.optFloat(key: String): Float? = (this.opt(key) as Number?)?.toFloat()


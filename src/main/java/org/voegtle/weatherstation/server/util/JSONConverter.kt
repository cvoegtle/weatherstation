package org.voegtle.weatherstation.server.util

import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.StatisticsSet
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import java.text.SimpleDateFormat
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
    json.putOpt("watt", currentWeatherData.watt)

    json.put("location", locationProperties.city)
    json.put("location_short", locationProperties.cityShortcut)
    json.putOpt("localtime", currentWeatherData.localTime)

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

  fun toJsonLegacy(currentWeatherData: UnformattedWeatherDTO, extended: Boolean): JSONObject {
    val json = WeatherJSONObject()
    json.put("timestamp", currentWeatherData.time)
    json.put("temperature", currentWeatherData.temperature)
    if (currentWeatherData.insideTemperature != null) {
      json.put("inside_temperature", currentWeatherData.insideTemperature)
    }
    json.put("humidity", currentWeatherData.humidity)
    if (currentWeatherData.insideHumidity != null) {
      json.put("inside_humidity", currentWeatherData.insideHumidity)
    }
    json.put("rain", currentWeatherData.rainLastHour)
    json.put("rain_today", currentWeatherData.rainToday)
    json.put("raining", currentWeatherData.isRaining)
    json.put("wind", multiply(currentWeatherData.windspeed, locationProperties.windMultiplier))
    if (currentWeatherData.watt != null) {
      json.put("watt", currentWeatherData.watt)
    }

    json.put("location", locationProperties.city)
    json.put("location_short", locationProperties.cityShortcut)

    json.put("id", locationProperties.location)
    if (extended) {
      json.put("forecast", locationProperties.weatherForecast)
    }

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

    var previousRain: Float = 0.0f
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

      val rain = wds.dailyRain - previousRain
      if (rain > 0) {
        json.put("rain", rain)
      }
      previousRain = wds.dailyRain
      json.put("wind", multiply(wds.windspeed, locationProperties.windMultiplier))
      json.put("windMax", multiply(wds.windspeedMax, locationProperties.windMultiplier))

      json.put("watt", wds.watt)
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


  fun toJsonAggregated(list: List<AggregatedWeatherDataSet>, extended: Boolean): ArrayList<JSONObject> {
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
      if (extended) {
        json.put("kwh", wds.kwh)
      }
      jsonObjects.add(json)
    }
    return jsonObjects
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


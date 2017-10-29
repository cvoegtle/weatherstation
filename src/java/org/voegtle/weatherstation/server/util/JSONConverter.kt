package org.voegtle.weatherstation.server.util

import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.StatisticsSet
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO
import org.voegtle.weatherstation.server.persistence.LocationProperties
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet
import java.text.SimpleDateFormat
import java.util.*

class JSONConverter(private val locationProperties: LocationProperties) {

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

      json.put("watt", wds.watt)

      jsonObjects.add(json)
    }

    return jsonObjects
  }

  private fun multiply(number: Float?, factor: Float?): Float? {
    if (number != null) {
      return number * (factor ?: 1.0f)
    }
    return null
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
    val weatherDTO = CacheWeatherDTO()
    val json = JSONObject(encodedWeatherData)
    val timestamp = json.getString("timestamp")
    weatherDTO.time = Date(timestamp)

    weatherDTO.id = json.getString("id")
    weatherDTO.forecast = json.getString("forecast")
    weatherDTO.location = json.getString("location")
    weatherDTO.locationShort = json.getString("location_short")

    val temperature = json.get("temperature") as Number
    weatherDTO.temperature = temperature.toFloat()

    if (json.has("localtime")) {
      weatherDTO.localTime = json.get("localtime") as String
    }

    if (json.has("inside_temperature")) {
      val insideTemperature = json.get("inside_temperature") as Number
      weatherDTO.insideTemperature = insideTemperature.toFloat()
    }

    val humidity = json.get("humidity") as Number
    weatherDTO.humidity = humidity.toFloat()

    if (json.has("inside_humidity")) {
      val insideHumidity = json.get("inside_humidity") as Number
      weatherDTO.insideHumidity = insideHumidity.toFloat()
    }

    if (json.has("watt")) {
      val watt = json.get("watt") as Number
      weatherDTO.watt = watt.toFloat()
    }

    if (json.has("rain")) {
      val rain = json.get("rain") as Number
      weatherDTO.rainLastHour = rain.toFloat()
    }

    if (json.has("rain_today")) {
      val rainToday = json.get("rain_today") as Number
      weatherDTO.rainToday = rainToday.toFloat()
    }

    if (json.has("raining")) {
      weatherDTO.isRaining = json.getBoolean("raining")
    }

    if (json.has("wind")) {
      val wind = json.get("wind") as Number
      weatherDTO.windspeed = wind.toFloat()
    }

    if (json.has("latitude")) {
      val latitude = json.get("latitude") as Number
      weatherDTO.latitude = latitude.toFloat()
    }

    if (json.has("longitude")) {
      val longitude = json.get("longitude") as Number
      weatherDTO.longitude = longitude.toFloat()
    }
    return weatherDTO
  }

  private val FORMAT_DATE = "yyyy-MM-dd"
}

package org.voegtle.weatherstation.server.util

import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.StatisticsSet
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import java.util.ArrayList
import java.util.logging.Logger

class JSONConverter(private val locationProperties: LocationProperties) {
  private val log = Logger.getLogger("JSONConverter")

  fun toJson(rain: RainDTO): JSONObject {
    val json = WeatherJSONObject()
    json.put("lastHour", rain.lastHour)
    json.put("today", rain.today)
    json.put("yesterday", rain.yesterday)
    json.put("lastWeek", rain.lastWeek)
    json.put("last30days", rain.last30Days)
    return json
  }

  fun toJson(stats: Statistics): JSONObject {
    val json = WeatherJSONObject()
    json.put("id", locationProperties.location)

    val jsonObjects = ArrayList<JSONObject>()
    stats.rainLastHour?.let {
      if (it > 0.0) {
        val lastHour = StatisticsSet()
        lastHour.addRain(it)
        jsonObjects.add(toJson(Statistics.TimeRange.lastHour, lastHour))
      }
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


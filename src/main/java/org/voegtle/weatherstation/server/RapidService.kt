package org.voegtle.weatherstation.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.rapid.RapidDataSet
import org.voegtle.weatherstation.server.util.parseUtcDate
import java.util.HashMap
import java.util.logging.Logger
import javax.cache.Cache
import javax.cache.CacheManager


@RestController class RapidService {
  @Autowired
  private val objectMapper: ObjectMapper? = null
  private var cache: Cache = createCache()

  private val pm = PersistenceManager()


  val log = Logger.getLogger("RapidService")

  @GetMapping("/weatherstation/rapid")
  fun receive(@RequestParam ID: String,
              @RequestParam PASSWORD: String,
              @RequestParam dateutc: String,
              @RequestParam temp: Float,
              @RequestParam humidity: Int,
              @RequestParam barometer: Float,
              @RequestParam dailyrain: Float,
              @RequestParam rain: Float,
              @RequestParam UV: Float?,
              @RequestParam solarradiation: Float?,
              @RequestParam winddir: Int?,
              @RequestParam windspeed: Float?,
              @RequestParam windgust: Float?,
              @RequestParam indoortemp: Float?,
              @RequestParam indoorhumidity: Float?): String {
    validateReceivedRequest(fetchLocationProperties(), ID, PASSWORD)
    storeReceivedDataInCache(dateutc, temp, humidity, barometer, dailyrain, rain, UV, solarradiation, winddir, windspeed, windgust, indoortemp,
                             indoorhumidity)
    return cache.size.toString()
  }

  private fun validateReceivedRequest(fetchLocationProperties: LocationProperties, id: String, secret: String) {

  }

  private fun storeReceivedDataInCache(dateutc: String, temp: Float, humidity: Int, barometer: Float,
                                       dailyrain: Float, rain: Float, UV: Float?, solarradiation: Float?,
                                       winddir: Int?, windspeed: Float?, windgust: Float?, indoortemp: Float?,
                                       indoorhumidity: Float?) {
    val dataset = RapidDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer, dailyRain = dailyrain,
                               rain = rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)
    val jsonDataset = objectMapper!!.writeValueAsString(dataset)
    cache.set(dataset.time, jsonDataset)
  }

  private fun fetchLocationProperties(): LocationProperties {
    val jsonLocation = cache.get("location")
    if (jsonLocation == null) {
      val locationProperties = pm.fetchLocationProperties()
      val json = objectMapper!!.writeValueAsString(locationProperties)
      cache.set("location", json)
      return locationProperties
    } else {
      return objectMapper!!.readValue(jsonLocation as String, LocationProperties::class.java)
    }
  }

  private fun createCache(): Cache {
    val cacheFactory = CacheManager.getInstance().cacheFactory
    return cacheFactory.createCache(HashMap<Any, Any>())
  }
}

package org.voegtle.weatherstation.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.appengine.api.log.InvalidRequestException
import com.sun.corba.se.impl.util.RepositoryId.cache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.util.HashService
import org.voegtle.weatherstation.server.util.parseUtcDate
import org.voegtle.weatherstation.server.weewx.AggregatedRapidDataSet
import org.voegtle.weatherstation.server.weewx.RapidCache
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.util.logging.Logger
import javax.annotation.PostConstruct


@RestController class RapidService {
  @Autowired
  private val objectMapper: ObjectMapper? = null
  private val pm = PersistenceManager()
  private val rapidCache = RapidCache()


  val log = Logger.getLogger("RapidService")

  @PostConstruct fun init() {
    rapidCache.objectMapper = this.objectMapper
  }

  @GetMapping("/weatherstation/weewx")
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
    val dataset = WeewxDataSet(time = parseUtcDate(dateutc), temperature = temp, humidity = humidity, barometer = barometer, dailyRain = dailyrain,
                               rain = rain, UV = UV, solarRadiation = solarradiation, windDirection = winddir, windSpeed = windspeed,
                               windGust = windgust, indoorTemperature = indoortemp, indoorHumidity = indoorhumidity)
    var aggregatedDataSet = rapidCache.getLatest()
    if (aggregatedDataSet == null) {
      aggregatedDataSet = AggregatedRapidDataSet(dataset)
    } else {
      aggregatedDataSet.add(dataset)
    }

    storeReceivedDataInCache(aggregatedDataSet)
    return cache.size.toString()
  }

  private fun validateReceivedRequest(locationProperties: LocationProperties, id: String, secret: String) {
    if (locationProperties.location != id || !isSecretValid(locationProperties, secret)) {
      log.warning("wrong credentials")
      throw InvalidRequestException("Credentials not valid")
    }
    log.info("Good credentials")
  }

  private fun storeReceivedDataInCache(aggregatedDataSet: AggregatedRapidDataSet) {
    rapidCache.save(aggregatedDataSet)
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

  internal fun isSecretValid(locationProperties: LocationProperties, secret: String?): Boolean {
    val secretHash = locationProperties.secretHash
    val calculateHash = HashService.calculateHash(secret)
    log.info("secret= $secret, hash=$calculateHash, secretHash=$secretHash")
    return secretHash == calculateHash
  }

}

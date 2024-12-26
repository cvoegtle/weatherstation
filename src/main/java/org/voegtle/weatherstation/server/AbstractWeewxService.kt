package org.voegtle.weatherstation.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.appengine.api.log.InvalidRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.util.HashService
import org.voegtle.weatherstation.server.weewx.RapidCache
import java.util.logging.Logger
import javax.annotation.PostConstruct

abstract class AbstractWeewxService(val log: Logger) {
  @Autowired
  private val objectMapper: ObjectMapper? = null
  val pm = PersistenceManager()
  val rapidCache = RapidCache()

  @PostConstruct fun init() {
    rapidCache.objectMapper = this.objectMapper
  }

  fun validateReceivedRequest(locationProperties: LocationProperties, id: String, secret: String) {
    if (locationProperties.location != id || !isSecretValid(locationProperties, secret)) {
      log.warning("wrong credentials")
      throw InvalidRequestException("Credentials not valid")
    }
  }

  fun validateReceivedRequest(locationProperties: LocationProperties, secret: String) {
    if (!isSecretValid(locationProperties, secret)) {
      log.warning("wrong credentials")
      throw InvalidRequestException("Credentials not valid")
    }
  }

  fun fetchLocationProperties(): LocationProperties {
    var locationProperties = rapidCache.getLocationProperties()
    if (locationProperties == null) {
      locationProperties = pm.fetchLocationProperties()
      rapidCache.saveLocationProperties(locationProperties)
      return locationProperties
    }
    return locationProperties
  }

  private fun isSecretValid(locationProperties: LocationProperties, secret: String?): Boolean {
    val secretHash = locationProperties.secretHash
    val calculateHash = HashService.calculateHash(secret)
    log.info("secret= $secret, hash=$calculateHash, secretHash=$secretHash")
    return secretHash == calculateHash
  }

  private fun createLocationProperties(): LocationProperties {
    val lp = LocationProperties()
    lp.location = "testwetter"
    lp.address = "Entwicklungs Station"
    lp.cityShortcut = "DEV"
    lp.city = "Oxengasse"
    lp.weatherForecast = "https://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage"
    lp.secretHash = "2fe3974d34634baf28c732f4793724f11e4a0813a84030f962187b3844485ae4"
    lp.readHash = "a883d58dbbb62d60da3893c9822d19e43bc371d20ccc5bfdb341f2b120eea54c"
    lp.expectedDataSets = 500
    lp.expectedRequests = 500
    lp.timezone = "Europe/Berlin"
    lp.longitude=7.862207f
    lp.latitude=48.02387f

    return lp
  }

}

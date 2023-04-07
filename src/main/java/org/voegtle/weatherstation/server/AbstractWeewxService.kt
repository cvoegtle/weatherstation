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


}

package org.voegtle.weatherstation.server.persistence

import com.google.appengine.api.datastore.Key

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class WeatherLocation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var key: Key? = null

  var location: String? = null
  var host: String? = null
  var isForwardSecret: Boolean = false
  var readHash: String? = null

}

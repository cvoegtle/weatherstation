package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import java.util.*

@Entity data class SolarDataSet(
  @Id var id: Long? = null,
  @Index var time: Date = Date(),
  var powerProduction: Float = 0.0f,
  var totalPowerProduction: Float = 0.0f,
  var powerFeed: Float = 0.0f
)

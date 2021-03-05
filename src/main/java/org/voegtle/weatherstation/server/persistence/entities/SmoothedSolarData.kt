package org.voegtle.weatherstation.server.persistence.entities

import java.util.Date

data class SmoothedSolarData(var time: Date = Date(),
                             var powerProduction: Float? = null,
                             var powerFeed: Float? = null)
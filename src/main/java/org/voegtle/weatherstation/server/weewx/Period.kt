package org.voegtle.weatherstation.server.weewx

import java.util.Date

data class Period(var start:Date = Date(), var end: Date = Date()) {
  fun length() = end.time - start.time
}
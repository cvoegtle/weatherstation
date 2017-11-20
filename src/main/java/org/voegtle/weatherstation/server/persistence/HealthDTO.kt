package org.voegtle.weatherstation.server.persistence

import java.io.Serializable
import java.util.Date

class HealthDTO(val day: Date, var requests: Int, var lines: Int, var persisted: Int) : Serializable {
  fun incrementRequests() {
    this.requests++
  }

  fun incrementLines(newLines: Int) {
    this.lines += newLines
  }

  fun incrementPersisted(persisted: Int) {
    this.persisted += persisted
  }
}

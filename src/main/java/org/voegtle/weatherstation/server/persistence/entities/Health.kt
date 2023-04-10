package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import org.voegtle.weatherstation.server.persistence.HealthDTO
import java.util.*

@Entity
class Health {
    @Id
    var id: Long? = null
    var day: Date? = null
    var requests = 0
    var lines = 0
    var persisted = 0

    constructor()
    constructor(day: Date?) {
        this.day = day
    }

    fun fromDTO(dto: HealthDTO) {
        day = dto.day
        requests = dto.requests
        lines = dto.lines
        persisted = dto.persisted
    }

    fun toDTO(): HealthDTO {
        val day = Date(day!!.time) // convert from datanucleus date to java.util.Date
        return HealthDTO(day, requests, lines, persisted)
    }
}

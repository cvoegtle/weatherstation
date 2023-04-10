package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id

@Entity
class Contact {
    @Id
    var id: Long? = null
    var mailAdress: String? = null
    var name: String? = null
    var isReceiveDailyStatus = false
    var isReceiveIncidentReports = false
}

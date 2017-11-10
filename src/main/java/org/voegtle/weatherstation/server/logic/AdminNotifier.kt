package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.persistence.HealthDTO
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.logging.Logger
import javax.mail.Transport

class AdminNotifier(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {
  private val log = Logger.getLogger("AdminNotifierLogger")

  private val dateUtil: DateUtil = locationProperties.dateUtil
  private val healthProvider = HealthProvider(pm, locationProperties)
  private val reportCreator = HealthReportCreator(locationProperties)

  fun sendNotifications() {
    val health = healthProvider.get(dateUtil.yesterday)
    val isIncident = checkForIncident(health)

    pm.fetchContacts()
        .filter { it.isReceiveDailyStatus || isIncident && it.isReceiveIncidentReports }
        .map { reportCreator.createMessage(it, health, isIncident) }
        .forEach { Transport.send(it) }
  }

  private fun checkForIncident(health: HealthDTO): Boolean {
    val expectedDataSets = locationProperties.expectedDataSets
    val expectedRequests = locationProperties.expectedRequests
    val datasetFactor = (expectedDataSets!! - health.persisted).toDouble() / expectedDataSets
    val requestFactor = (expectedRequests!! - health.requests).toDouble() / expectedRequests
    val incidentDetected = datasetFactor > 0.1 || requestFactor > 0.1
    log.warning("incident: $incidentDetected, Requestfaktor: $requestFactor, DatasetFaktor: $datasetFactor")
    return incidentDetected
  }

}

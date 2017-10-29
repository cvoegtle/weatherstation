package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.persistence.Contact;
import org.voegtle.weatherstation.server.persistence.HealthDTO;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.util.DateUtil;

import javax.mail.Message;
import javax.mail.Transport;
import java.util.List;
import java.util.logging.Logger;

public class AdminNotifier {
  protected static final Logger log = Logger.getLogger("AdminNotifierLogger");

  private final PersistenceManager pm;
  private final DateUtil dateUtil;
  private final LocationProperties locationProperties;
  private final HealthProvider healthProvider;
  private final HealthReportCreator reportCreator;

  public AdminNotifier(PersistenceManager pm, LocationProperties locationProperties) {
    this.pm = pm;
    this.dateUtil = locationProperties.getDateUtil();
    this.locationProperties = locationProperties;
    this.healthProvider = new HealthProvider(pm, locationProperties);
    this.reportCreator = new HealthReportCreator(locationProperties);
  }

  public void notifiy() {
    try {
      HealthDTO health = healthProvider.get(dateUtil.yesterday());
      boolean isIncident = checkHealth(health);

      List<Contact> contacts = pm.fetchContacts();
      for (Contact contact : contacts) {
        if (contact.isReceiveDailyStatus() || (isIncident && contact.isReceiveIncidentReports())) {
          Message notification = reportCreator.createMessage(contact, health, isIncident);
          Transport.send(notification);
        }
      }


    } catch (Exception ex) {
      log.warning("failed to send mail");
      throw new RuntimeException(ex);
    }
  }

  private boolean checkHealth(HealthDTO health) {
    Integer expectedDataSets = locationProperties.getExpectedDataSets();
    Integer expectedRequests = locationProperties.getExpectedRequests();
    double datasetFactor = ((double)(expectedDataSets - health.getPersisted())) / expectedDataSets;
    double requestFactor = ((double)(expectedRequests - health.getRequests())) / expectedRequests;
    boolean healthy = ( datasetFactor > 0.1) || (requestFactor > 0.1);
    log.warning("Healthy: " + healthy + ", Requestfaktor: " + requestFactor + ", DatasetFaktor: " + datasetFactor);
    return healthy;
  }
}

package org.voegtle.weatherstation.server.logic;

import com.google.appengine.api.utils.SystemProperty;
import org.voegtle.weatherstation.server.persistence.Contact;
import org.voegtle.weatherstation.server.persistence.HealthDTO;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.util.DateUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class AdminNotifier {
  protected static final Logger log = Logger.getLogger("AdminNotifierLogger");

  private final PersistenceManager pm;
  private final DateUtil dateUtil;
  private final LocationProperties locationProperties;
  private final HealthProvider healthProvider;

  public AdminNotifier(PersistenceManager pm, LocationProperties locationProperties) {
    this.pm = pm;
    this.dateUtil = locationProperties.getDateUtil();
    this.locationProperties = locationProperties;
    this.healthProvider = new HealthProvider(pm, locationProperties);
  }

  public void notifiy() {
    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      HealthDTO health = healthProvider.get(dateUtil.getYesterday());

      boolean isIncident = checkHealth(health);

      List<Contact> contacts = pm.fetchContacts();
      for (Contact contact : contacts) {
        if (contact.isReceiveDailyStatus() || (isIncident && contact.isReceiveIncidentReports())) {
          Message notification = new MimeMessage(session);
          createMessage(notification, contact, health);
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
    return (((expectedDataSets - health.getPersisted()) / expectedDataSets) > 0.1) ||
        (((expectedRequests - health.getRequests()) / expectedRequests) > 0.1);
  }

  private void createMessage(Message notification, Contact contact, HealthDTO health) throws UnsupportedEncodingException, MessagingException {
    String appId = SystemProperty.applicationId.get();
    notification.setFrom(new InternetAddress("admin@" + appId + ".appspotmail.com", "Administrator"));
    notification.setRecipient(Message.RecipientType.TO, new InternetAddress(contact.getMailAdress(), contact.getName()));

    String subject = appId + " - Statusbericht vom " + dateUtil.formatAsDate(health.getDay());
    notification.setSubject(subject);
    String htmlBody = "<h1>" + subject + "</h1>" +
        "<b>Requests:</b><br>" + health.getRequests() + " of expected " +locationProperties.getExpectedRequests() + " requests<br><br>" +
        "<b>Lines</b><br>valid: " + health.getLines() + ", invalid: " + (health.getLines() - health.getPersisted()) + "<br>" +
        "expected: " + locationProperties.getExpectedDataSets() + ", diff: " + (locationProperties.getExpectedDataSets() - health.getPersisted());
    Multipart mp = new MimeMultipart();

    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(htmlBody, "text/html");
    mp.addBodyPart(htmlPart);

    notification.setContent(mp);
  }
}

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
  private final HealthProvider healthProvider;

  public AdminNotifier(PersistenceManager pm, LocationProperties locationProperties) {
    this.pm = pm;
    this.dateUtil = locationProperties.getDateUtil();
    this.healthProvider = new HealthProvider(pm, locationProperties);
  }

  public void notifiy() {
    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);

      List<Contact> contacts = pm.fetchContacts();
      for (Contact contact : contacts) {
        if (contact.isReceiveDailyStatus()) {
          HealthDTO health = healthProvider.get(dateUtil.getYesterday());
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

  private void createMessage(Message notification, Contact contact, HealthDTO health) throws UnsupportedEncodingException, MessagingException {
    String appId = SystemProperty.applicationId.get();
    notification.setFrom(new InternetAddress("admin@" + appId + ".appspotmail.com", "Administrator"));
    notification.setRecipient(Message.RecipientType.TO, new InternetAddress(contact.getMailAdress(), contact.getName()));
    notification.setSubject(appId + " - Statusbericht vom " + dateUtil.formatAsDate(health.getDay()));
    String htmlBody = "Requests: " + health.getRequests() + "<br>" +
        "Lines: " + health.getLines() + "<br>" +
        "Persisted: " + health.getPersisted();
    Multipart mp = new MimeMultipart();

    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(htmlBody, "text/html");
    mp.addBodyPart(htmlPart);

    notification.setContent(mp);
  }
}

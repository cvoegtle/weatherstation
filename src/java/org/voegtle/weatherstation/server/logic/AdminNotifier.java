package org.voegtle.weatherstation.server.logic;

import com.google.appengine.api.utils.SystemProperty;
import org.voegtle.weatherstation.server.persistence.HealthDTO;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.util.DateUtil;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.util.logging.Logger;

public class AdminNotifier {
  protected static final Logger log = Logger.getLogger("AdminNotifierLogger");

  private final DateUtil dateUtil;

  public AdminNotifier(LocationProperties locationProperties) {
    this.dateUtil = locationProperties.getDateUtil();
  }

  public void notifiy(HealthDTO health) {
    try {
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);

      Message notification = new MimeMessage(session);
      notification.setFrom(new InternetAddress("admin@" + SystemProperty.applicationId.get() + ".appspotmail.com", "Administrator"));
      notification.setRecipient(Message.RecipientType.TO, new InternetAddress("christian@voegtle.org", "Christian Vögtle"));
      notification.setSubject("Statusbericht vom " + dateUtil.formatAsDate(health.getDay()));
      String htmlBody = "Requests: " + health.getRequests() + "<br>" +
          "Lines: " + health.getLines() + "<br>" +
          "Persisted: " + health.getPersisted();
      Multipart mp = new MimeMultipart();

      MimeBodyPart htmlPart = new MimeBodyPart();
      htmlPart.setContent(htmlBody, "text/html");
      mp.addBodyPart(htmlPart);

      notification.setContent(mp);

      Transport.send(notification);
    } catch (Exception ex) {
      log.warning("failed to send mail");
      throw new RuntimeException(ex);
    }
  }
}

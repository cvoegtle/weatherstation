package org.voegtle.weatherstation.server.logic;

import com.google.appengine.api.utils.SystemProperty;
import org.voegtle.weatherstation.server.persistence.entities.Contact;
import org.voegtle.weatherstation.server.persistence.HealthDTO;
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties;
import org.voegtle.weatherstation.server.util.DateUtil;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

class HealthReportCreator {
  private final Session session;
  private final DateUtil dateUtil;
  private LocationProperties locationProperties;

  HealthReportCreator(LocationProperties locationProperties) {
    this.locationProperties = locationProperties;
    this.dateUtil = locationProperties.getDateUtil();
    Properties props = new Properties();
    session = Session.getDefaultInstance(props, null);
  }


  Message createMessage(Contact contact, HealthDTO health, boolean isIncident) throws UnsupportedEncodingException, MessagingException {
    Message notification = new MimeMessage(session);
    String appId = SystemProperty.applicationId.get();
    notification.setFrom(new InternetAddress("admin@" + appId + ".appspotmail.com", "Administrator"));
    notification.setRecipient(Message.RecipientType.TO, new InternetAddress(contact.getMailAdress(), contact.getName()));

    String subject = getSubject(health, isIncident, appId);
    notification.setSubject(subject);
    String htmlBody = "<h1><font color='" + (isIncident ? "red" : "black") + "'>" + subject + "</font></h1>" +
        "<b>Requests:</b><br>" + health.getRequests() + " of expected " + locationProperties.getExpectedRequests() + " requests<br><br>" +
        "<b>Lines</b><br>valid: " + health.getLines() + ", invalid: " + (health.getLines() - health.getPersisted()) + "<br>" +
        "expected: " + locationProperties.getExpectedDataSets() + ", diff: " + (locationProperties.getExpectedDataSets() - health.getPersisted());
    Multipart mp = new MimeMultipart();

    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(htmlBody, "text/html");
    mp.addBodyPart(htmlPart);

    notification.setContent(mp);
    return notification;
  }

  private String getSubject(HealthDTO health, boolean isIncident, String appId) {
    String reportType = isIncident ? "Problembericht" : "Statusbericht";
    return appId + " - " + reportType + " vom " + dateUtil.formatAsDate(health.getDay());
  }
}

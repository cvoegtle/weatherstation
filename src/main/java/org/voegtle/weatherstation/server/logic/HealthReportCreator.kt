package org.voegtle.weatherstation.server.logic

import com.google.appengine.api.utils.SystemProperty
import org.voegtle.weatherstation.server.persistence.HealthDTO
import org.voegtle.weatherstation.server.persistence.entities.Contact
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.util.DateUtil
import java.io.UnsupportedEncodingException
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

internal class HealthReportCreator(private val locationProperties: LocationProperties) {
  private val session: Session = Session.getDefaultInstance(Properties(), null)
  private val dateUtil: DateUtil = locationProperties.dateUtil

  @Throws(UnsupportedEncodingException::class, MessagingException::class)
  fun createMessage(contact: Contact, health: HealthDTO, isIncident: Boolean): Message {
    val notification = MimeMessage(session)
    val appId = SystemProperty.applicationId.get()
    notification.setFrom(InternetAddress("admin@$appId.appspotmail.com", "Administrator"))
    notification.setRecipient(Message.RecipientType.TO, InternetAddress(contact.mailAdress, contact.name))

    val subject = getSubject(health, isIncident, appId)
    notification.subject = subject
    val htmlBody = "<h1><font color='${if (isIncident) "red" else "black"}'>$subject</font></h1>" +
        "<b>Requests:</b><br>${health.requests} of expected ${locationProperties.expectedRequests} requests<br><br>" +
        "<b>Lines</b><br>valid: ${health.lines}, invalid: ${health.lines - health.persisted}<br>" +
        "expected: ${locationProperties.expectedDataSets}, diff: ${locationProperties.expectedDataSets!! - health.persisted}"
    val mp = MimeMultipart()

    val htmlPart = MimeBodyPart()
    htmlPart.setContent(htmlBody, "text/html")
    mp.addBodyPart(htmlPart)

    notification.setContent(mp)
    return notification
  }

  private fun getSubject(health: HealthDTO, isIncident: Boolean, appId: String): String {
    val reportType = if (isIncident) "Problembericht" else "Statusbericht"
    return "$appId - $reportType vom ${dateUtil.formatAsDate(health.day)}"
  }
}

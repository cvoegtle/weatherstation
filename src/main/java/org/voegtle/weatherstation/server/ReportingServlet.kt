package org.voegtle.weatherstation.server

import org.voegtle.weatherstation.server.logic.AdminNotifier
import org.voegtle.weatherstation.server.request.ResponseCode

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

class ReportingServlet : AbstractServlet() {

  @Throws(ServletException::class, IOException::class)
  override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val notifier = AdminNotifier(pm, locationProperties)
    notifier.sendNotifications()
    returnResult(response, ResponseCode.ACKNOWLEDGE)
  }
}

package org.voegtle.weatherstation.server

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.voegtle.weatherstation.server.logic.AdminNotifier
import org.voegtle.weatherstation.server.request.ResponseCode
import java.io.IOException

class ReportingServlet : AbstractServlet() {

  @Throws(ServletException::class, IOException::class)
  override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val notifier = AdminNotifier(pm, locationProperties!!)
    notifier.sendNotifications()
    returnResult(response, ResponseCode.ACKNOWLEDGE)
  }
}

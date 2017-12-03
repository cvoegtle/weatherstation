package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.util.DateUtil

import javax.servlet.http.HttpServletRequest

class IncomingUrlParameter(request: HttpServletRequest, dateUtil: DateUtil) : AbstractUrlParameter(request, dateUtil) {

  private val PARAM_DATA = "data"
  private val PARAM_LOCATION = "location"
  private val PARAM_SECRET = "secret"

  val data: String? = getUrlParameter(PARAM_DATA)
  val location: String? = getUrlParameter(PARAM_LOCATION)
  val secret: String? = getUrlParameter(PARAM_SECRET)
}

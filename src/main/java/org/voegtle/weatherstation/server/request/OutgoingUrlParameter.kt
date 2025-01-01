package org.voegtle.weatherstation.server.request

import jakarta.servlet.http.HttpServletRequest
import org.voegtle.weatherstation.server.util.DateUtil

class OutgoingUrlParameter(request: HttpServletRequest, dateUtil: DateUtil) : UrlParameter(request, dateUtil, DataType.UNDEFINED)

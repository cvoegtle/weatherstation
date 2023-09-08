package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.util.DateUtil

import javax.servlet.http.HttpServletRequest

class RepairUrlParameter(request: HttpServletRequest, dateUtil: DateUtil) : UrlParameter(request, dateUtil,
                                                                                         DataType.UNDEFINED) {
}

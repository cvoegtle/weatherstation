package org.voegtle.weatherstation.server.request

import jakarta.servlet.http.HttpServletRequest
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*

class SolarUrlParameter(request: HttpServletRequest, dateUtil: DateUtil): AbstractUrlParameter(request, dateUtil) {
    private val PARAM_ID = "ID"
    private val PARAM_PASSWORD = "PASSWORD"
    private val PARAM_DATE = "date"
    private val PARAM_POWERFEED = "powerFeed"
    private val PARAM_POWERPRODUCTION = "powerProduction"
    private val PARAM_TOTALPOWERPRODUCTION = "totalPowerProduction"

    val id: String? = getUrlParameter(PARAM_ID)
    val password: String? = getUrlParameter(PARAM_PASSWORD)
    val date: Date? = getUrlParameterDate(PARAM_DATE, true)
    val powerFeed = getUrlParameter(PARAM_POWERFEED)
    val powerProduction = getUrlParameter(PARAM_POWERPRODUCTION)
    val totalPowerProduction = getUrlParameter(PARAM_TOTALPOWERPRODUCTION)
}

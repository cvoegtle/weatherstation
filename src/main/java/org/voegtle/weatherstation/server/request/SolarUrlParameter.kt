package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import javax.servlet.http.HttpServletRequest

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
    val powerFeed = getUrlParameterInteger(PARAM_POWERFEED)
    val powerProduction = getUrlParameterInteger(PARAM_POWERPRODUCTION)
    val totalPowerProduction = getUrlParameterInteger(PARAM_TOTALPOWERPRODUCTION)
}

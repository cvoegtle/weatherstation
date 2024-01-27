package org.voegtle.weatherstation.server

import org.voegtle.weatherstation.server.persistence.entities.SolarDataSet
import org.voegtle.weatherstation.server.request.ResponseCode
import org.voegtle.weatherstation.server.request.SolarUrlParameter
import org.voegtle.weatherstation.server.request.ValidationException
import java.io.IOException
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SolarServlet : AbstractServlet() {
    val intervalChecker = TimeBetweenRequestsChecker("solar_request")

    @Throws(ServletException::class, IOException::class)
    public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        val param = SolarUrlParameter(request, locationProperties!!.dateUtil)

        try {
            log.warning("date: ${param.date} powerFeed: <ignored> powerProduction: ${param.powerProduction} totalPowerProduction: ${param.totalPowerProduction}")
            assertRequestParamComplete(param)
            assertSecretValid(param.password)
            assertCorrectLocation(param.id)

            val dataset = SolarDataSet(
                time = param.date!!,
                powerFeed = 0.0f,
                powerProduction = param.powerProduction!!.toFloat(),
                totalPowerProduction = param.totalPowerProduction!!.toFloat()
            )

            assertEnoughTimeElapsedSinceLastRequest(dataset.time)

            pm.makePersistent(dataset)
            returnResult(response, ResponseCode.ACKNOWLEDGE)
        } catch (ve: ValidationException) {
            returnResult(response, ve.responseCode)
        }
    }

    private fun assertRequestParamComplete(param: SolarUrlParameter) {
        if (param.date == null || param.powerProduction == null || param.totalPowerProduction == null) {
            throw ValidationException(ResponseCode.PARSE_ERROR)
        }
    }

    private fun assertEnoughTimeElapsedSinceLastRequest(requestTime: Date) {
        if (!intervalChecker.hasEnoughTimeElapsedSinceLastRequest(requestTime)) {
            throw ValidationException(ResponseCode.IGNORED)
        }
    }

}

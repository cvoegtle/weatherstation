package org.voegtle.weatherstation.server

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.voegtle.weatherstation.server.persistence.entities.SolarDataSet
import org.voegtle.weatherstation.server.request.ResponseCode
import org.voegtle.weatherstation.server.request.SolarUrlParameter
import org.voegtle.weatherstation.server.request.ValidationException
import java.io.IOException
import java.util.*

class SolarServlet : AbstractServlet() {
    val intervalChecker = TimeBetweenRequestsChecker("solar_request")

    @Throws(ServletException::class, IOException::class)
    public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        val param = SolarUrlParameter(request, locationProperties!!.dateUtil)

        try {
            log.info("date: ${param.date} powerFeed: ${param.powerFeed} powerProduction: ${param.powerProduction} totalPowerProduction: ${param.totalPowerProduction}")
            assertSecretValid(param.password)
            assertCorrectLocation(param.id)

            val dataset = SolarDataSet(
                time = param.date!!,
                powerFeed = toFloatOrNull(param.powerFeed),
                powerProduction = toFloatOrNull(param.powerProduction),
                totalPowerProduction = toFloatIfGreaterZeroElseNull(param.totalPowerProduction)
            )

            assertDataSetNotEmpty(dataset)
            assertEnoughTimeElapsedSinceLastRequest(dataset.time)

            pm.makePersistent(dataset)
            returnResult(response, ResponseCode.ACKNOWLEDGE)
        } catch (ve: ValidationException) {
            returnResult(response, ve.responseCode)
        }
    }

    private fun toFloatOrNull(value: String?): Float? {
        return try {
            value?.toFloat()
        } catch (ex: Exception) {
            null
        }
    }
    private fun toFloatIfGreaterZeroElseNull(value: String?): Float? {
        try {
            val intValue = value?.toInt()
            return if (intValue != null && intValue > 0) intValue.toFloat() else null
        } catch (ex: Exception) {
            return null
        }
    }

    private fun assertDataSetNotEmpty(dataSet: SolarDataSet) {
        if (dataSet.powerProduction == null && dataSet.totalPowerProduction == null && dataSet.powerFeed == null) {
            throw ValidationException(ResponseCode.PARSE_ERROR)
        }
    }

    private fun assertEnoughTimeElapsedSinceLastRequest(requestTime: Date) {
        if (!intervalChecker.hasEnoughTimeElapsedSinceLastRequest(requestTime)) {
            throw ValidationException(ResponseCode.IGNORED)
        }
    }

}

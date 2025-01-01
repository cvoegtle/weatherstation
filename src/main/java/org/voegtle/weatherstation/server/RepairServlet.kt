package org.voegtle.weatherstation.server

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.voegtle.weatherstation.server.logic.WeatherDataRepair
import org.voegtle.weatherstation.server.request.RepairUrlParameter
import org.voegtle.weatherstation.server.request.ResponseCode
import java.io.IOException

class RepairServlet : AbstractServlet() {

  @Throws(ServletException::class, IOException::class)
  override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {

    val param = RepairUrlParameter(request, locationProperties!!.dateUtil)

    if (isSecretValid(param.secret)) {
      val repairService = WeatherDataRepair(pm, locationProperties!!)
      val repaired = repairService.repair(param.begin!!, param.end)
      returnDetailedResult(response, repaired, false)
    } else {
      returnResult(response, ResponseCode.NOT_AUTHORIZED)
    }
  }

}

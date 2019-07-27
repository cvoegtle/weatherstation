package org.voegtle.weatherstation.server

import org.voegtle.weatherstation.server.logic.WeatherDataRepair
import org.voegtle.weatherstation.server.request.RepairUrlParameter
import org.voegtle.weatherstation.server.request.ResponseCode
import org.voegtle.weatherstation.server.util.UpgradeService
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RepairServlet : AbstractServlet() {

  @Throws(ServletException::class, IOException::class)
  override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {

    val param = RepairUrlParameter(request, locationProperties!!.dateUtil)
    
    if (isSecretValid(param.secret)) {
      if (param.upgrade) {
        val upgrader = UpgradeService(pm, locationProperties!!)
//        upgrader.upgradeAggregated()
        upgrader.upgradeSmoothed(param.begin!!, param.end)
        returnResult(response, ResponseCode.ACKNOWLEDGE)
      } else {
        val repairService = WeatherDataRepair(pm, locationProperties!!)
        val repaired = repairService.repair(param.begin!!, param.end)
        returnDetailedResult(response, repaired, false)
      }
    } else {
      returnResult(response, ResponseCode.NOT_AUTHORIZED)
    }
  }

}

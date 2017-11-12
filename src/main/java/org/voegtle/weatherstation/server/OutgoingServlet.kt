package org.voegtle.weatherstation.server

import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.request.DataType
import org.voegtle.weatherstation.server.request.OutgoingUrlParameter
import org.voegtle.weatherstation.server.util.DateUtil
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OutgoingServlet : AbstractServlet() {

  @Throws(ServletException::class, IOException::class)
  public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
    response.setHeader("Access-Control-Allow-Origin", "*")

    val weatherDataFetcher = WeatherDataFetcher(pm, locationProperties)

    val param = OutgoingUrlParameter(request, locationProperties!!.dateUtil)
    val authorized = isReadSecretValid(param.secret)

    if (param.type == DataType.AGGREGATED) {
      val result = weatherDataFetcher.getAggregatedWeatherData(param.begin, param.end)
      returnAggregatedResult(response, result, param.isExtended)
    } else if (param.type == DataType.CURRENT) {
      val currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted(authorized)
      returnCurrentWeatherData(response, currentWeatherData, param.isExtended, param.isNewFormat)
    } else if (param.type == DataType.RAIN) {
      val rainData = weatherDataFetcher.fetchRainData()
      writeResponse(response, jsonConverter!!.toJson(rainData))
    } else if (param.type == DataType.STATS) {
      val stats = weatherDataFetcher.fetchStatistics()
      writeResponse(response, jsonConverter!!.toJson(stats, param.isNewFormat))
    } else if (param.begin != null) {
      val result = weatherDataFetcher.fetchSmoothedWeatherData(param.begin, param.end)
      log.info("begin: " + param.begin + " end: " + param.end + " localTZ: " + param.isLocalTimezone +
                                   " localTimezone: " + locationProperties!!.dateUtil.timezone.displayName +
                                   " CEST: " + DateUtil.getTzCEST().displayName)

      returnDetailedResult(response, result, authorized)
    }

  }

  private fun returnCurrentWeatherData(response: HttpServletResponse, currentWeatherData: UnformattedWeatherDTO,
                                       extended: Boolean, newFormat: Boolean) {
    val json = if (newFormat) jsonConverter!!.toJson(currentWeatherData) else jsonConverter!!.toJsonLegacy(
        currentWeatherData, extended)
    writeResponse(response, json)
  }

  private fun returnAggregatedResult(response: HttpServletResponse, list: List<AggregatedWeatherDataSet>,
                                     extended: Boolean) {
    val jsonObjects = jsonConverter!!.toJsonAggregated(list, extended)
    writeResponse(response, jsonObjects)
  }

}

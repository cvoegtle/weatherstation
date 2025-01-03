package org.voegtle.weatherstation.server

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.request.DataType
import org.voegtle.weatherstation.server.request.OutgoingUrlParameter
import java.io.IOException
import java.util.*

class OutgoingServlet : AbstractServlet() {

  @Throws(ServletException::class, IOException::class)
  public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
    response.setHeader("Access-Control-Allow-Origin", "*")

    val weatherDataFetcher = WeatherDataFetcher(pm, locationProperties!!)

    val param = OutgoingUrlParameter(request, locationProperties!!.dateUtil)
    val authorized = isReadSecretValid(param.secret)

    when {
      param.type == DataType.AGGREGATED -> {
        val result = weatherDataFetcher.getAggregatedWeatherData(param.begin!!, param.end ?: Date())
        returnAggregatedResult(response, result)
      }
      param.type == DataType.CURRENT -> {
        val currentWeatherData = weatherDataFetcher.getLatestWeatherDataUnformatted(authorized)
        returnCurrentWeatherData(response, currentWeatherData)
      }
      param.type == DataType.RAIN -> {
        val rainData = weatherDataFetcher.fetchRainData()
        writeResponse(response, jsonConverter!!.toJson(rainData))
      }
      param.type == DataType.STATS -> {
        val stats = weatherDataFetcher.fetchStatistics()
        writeResponse(response, jsonConverter!!.toJson(stats))
      }
      param.begin != null -> {
        val result = weatherDataFetcher.fetchSmoothedWeatherData(param.begin, param.end!!)
        log.info(
            "begin: ${param.begin} end: ${param.end} localTZ: ${param.isLocalTimezone} localTimezone: ${locationProperties?.dateUtil?.timezone?.displayName}")

        returnDetailedResult(response, result, authorized)
      }
    }

  }

  private fun returnCurrentWeatherData(response: HttpServletResponse, currentWeatherData: UnformattedWeatherDTO) {
    val json = jsonConverter!!.toJson(currentWeatherData)
    writeResponse(response, json)
  }

  private fun returnAggregatedResult(response: HttpServletResponse, list: List<AggregatedWeatherDataSet>) {
    val jsonObjects = jsonConverter!!.toJsonAggregated(list)
    writeResponse(response, jsonObjects)
  }

}

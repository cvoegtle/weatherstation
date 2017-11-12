package org.voegtle.weatherstation.server.logic

import org.json.JSONObject
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import org.voegtle.weatherstation.server.util.JSONConverter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Logger

internal class WeatherDataForwarder(pm: PersistenceManager, locationProperties: LocationProperties) {
  private val log = Logger.getLogger(WeatherDataForwarder::class.simpleName)
  private val COMMUNICATION_TIMEOUT = 60000

  private val weatherDataFetcher = WeatherDataFetcher(pm, locationProperties)
  private val jsonConverter = JSONConverter(locationProperties)

  fun forwardLastDataset() {
    val latest = weatherDataFetcher.getLatestWeatherDataUnformatted(true)
    forward(jsonConverter.toJson(latest))
  }

  private fun forward(json: JSONObject) {
    try {
      val encodedBytes = json.toString().toByteArray(charset("ISO-8859-1"))

      val wetterCentral = URL("https://wettercentral.appspot.com/weatherstation/cache")
      val wetterConnection = wetterCentral.openConnection() as HttpURLConnection

      try {
        setConnectionParameters(wetterConnection)
        wetterConnection.outputStream.write(encodedBytes)
        wetterConnection.outputStream.close()
        log.info("forwarded <" + json.toString() + ">")
        read(wetterConnection.inputStream)
      } finally {
        wetterConnection.inputStream.close()
        wetterConnection.disconnect()
      }

    } catch (e: IOException) {
      log.warning("failed forwarding to wettercentral. " + e.message)
    }

  }

  private fun setConnectionParameters(wetterConnection: HttpURLConnection) {
    wetterConnection.connectTimeout = COMMUNICATION_TIMEOUT
    wetterConnection.readTimeout = COMMUNICATION_TIMEOUT
    wetterConnection.requestMethod = "POST"
    wetterConnection.doOutput = true
  }

  @Throws(IOException::class)
  private fun read(inputStream: InputStream): String {
    val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
    return reader.readLine()
  }

  private fun getLast(dataSets: List<WeatherDataSet>): WeatherDataSet {
    val lastIndex = dataSets.size - 1
    return dataSets[lastIndex]
  }

}

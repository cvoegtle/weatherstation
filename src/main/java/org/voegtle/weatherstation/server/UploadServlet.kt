package org.voegtle.weatherstation.server

import org.voegtle.weatherstation.server.logic.HealthProvider
import org.voegtle.weatherstation.server.logic.WeatherDataImporter
import org.voegtle.weatherstation.server.parser.DataLine
import org.voegtle.weatherstation.server.request.IncomingUrlParameter
import org.voegtle.weatherstation.server.request.ResponseCode
import java.io.BufferedReader
import java.io.IOException
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UploadServlet : AbstractInputServlet() {

  @Throws(ServletException::class, IOException::class)
  override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
    val result: String

    val param = IncomingUrlParameter(request, locationProperties.dateUtil)
    if (isSecretValid(param.secret)) {
      if (isCorrectLocation(param.location)) {
        val hp = HealthProvider(pm, locationProperties)
        val health = hp.get()
        health.incrementRequests()

        val lines = readInputLines(getContentStream(request, "$1"))
        health.incrementLines(lines.size)

        val importer = WeatherDataImporter(pm, locationProperties)
        result = importer.doImport(lines)

        health.incrementPersisted(importer.persisted)
        hp.update(health)
      } else {
        result = ResponseCode.WRONG_LOCATION
      }
    } else {
      AbstractServlet.log.warning("request without authorisation. Sent secret: " + param.secret)
      result = ResponseCode.NOT_AUTHORIZED
    }

    returnResult(response, result)
  }

  private fun readInputLines(reader: BufferedReader?): ArrayList<DataLine> {
    val lines = ArrayList<DataLine>()
    var line: String? = reader!!.readLine()
    while (line != null) {
      lines.add(DataLine(line))
      line = reader.readLine()
    }
    AbstractServlet.log.info("number of datalines: " + lines.size)
    for (dl in lines) {
      AbstractServlet.log.info(dl.toString())
    }
    return lines
  }

}

package org.voegtle.weatherstation.server

import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import javax.servlet.http.HttpServletRequest

abstract class AbstractInputServlet : AbstractServlet() {

  @Throws(IOException::class)
  protected fun readString(request: HttpServletRequest): String {
    val buffer = StringBuffer()
    val reader = getContentStream(request)
    var line: String? = reader.readLine()
    while (line != null) {
      buffer.append(line)
      line = reader.readLine()
    }
    return buffer.toString()
  }

  @Throws(IOException::class)
  protected fun getContentStream(request: HttpServletRequest, startPattern: String): BufferedReader? {
    val parameterNames = request.parameterNames
    while (parameterNames.hasMoreElements()) {
      val value = parameterNames.nextElement() as String
      if (value.startsWith(startPattern)) {
        return BufferedReader(StringReader(value))
      }
    }
    return null
  }

  @Throws(IOException::class)
  protected fun getContentStream(request: HttpServletRequest): BufferedReader {
    log.info(
        "returning request.getInputStream() length: ${request.contentLength}, encoding: ${request.characterEncoding}")
    return request.reader
  }

}

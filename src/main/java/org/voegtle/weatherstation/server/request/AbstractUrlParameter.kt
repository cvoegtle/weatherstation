package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.util.DateUtil
import org.voegtle.weatherstation.server.util.StringUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import javax.servlet.http.HttpServletRequest

abstract class AbstractUrlParameter protected constructor(private val request: HttpServletRequest,
                                                               private val dateUtil: DateUtil) {
  private val FORMAT_TIMESTAMP = "yyyy-MM-dd-HH:mm:ss"

  protected fun getUrlParameter(paramName: String): String? {
    val param = request.getParameter(paramName)
    return if ("" == param) null else param
  }

  protected fun getUrlParameterBoolean(paramName: String): Boolean {
    val param = request.getParameter(paramName)
    return param != null
  }

  protected fun getUrlParameterDate(paramName: String, localTimezone: Boolean): Date? {
    val param = request.getParameter(paramName)
    if (StringUtil.isEmpty(param)) {
      return null
    }

    val sdf = SimpleDateFormat(FORMAT_TIMESTAMP)
    var result: Date?
    try {
      result = sdf.parse(param)
      result = if (localTimezone) {
        dateUtil.fromLocalToGMT(result)
      } else {
        dateUtil.fromCESTtoGMT(result)
      }
    } catch (e: ParseException) {
      result = null
    }

    return result
  }

  @JvmOverloads protected fun getUrlParameterInteger(paramName: String, defaultValue: Int? = null): Int? {
    val param = request.getParameter(paramName)
    return if (StringUtil.isNotEmpty(param)) param.toInt() else defaultValue
  }

  protected fun getUrlParameterType(paramName: String, defaultType: DataType): DataType {
    val param = request.getParameter(paramName)
    var dataType = DataType.fromString(param)
    if (dataType == DataType.UNDEFINED) {
      dataType = defaultType
    }
    return dataType
  }

  protected fun getUrlParameterType(paramName: String): DataType {
    val param = request.getParameter(paramName)
    return DataType.fromString(param)
  }

}

package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import javax.servlet.http.HttpServletRequest

open class UrlParameter(request: HttpServletRequest, dateUtil: DateUtil,
                        defaultDataType: DataType) : AbstractUrlParameter(request, dateUtil) {

  private val PARAM_BEGIN = "begin"
  private val PARAM_END = "end"
  private val PARAM_SECRET = "secret"
  private val PARAM_EXTENDED = "ext"
  private val PARAM_TYPE = "type"
  private val PARAM_LOCAL_TIMEZONE = "local_timezone"

  val isLocalTimezone: Boolean = getUrlParameterBoolean(PARAM_LOCAL_TIMEZONE)

  val type: DataType = getUrlParameterType(PARAM_TYPE, defaultDataType)
  val begin: Date? = getUrlParameterDate(PARAM_BEGIN, isLocalTimezone)
  val end: Date? = getUrlParameterDate(PARAM_END, isLocalTimezone)
  val secret: String? = getUrlParameter(PARAM_SECRET)

  val isExtended: Boolean = getUrlParameterBoolean(PARAM_EXTENDED)

}

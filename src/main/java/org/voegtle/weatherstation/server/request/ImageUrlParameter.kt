package org.voegtle.weatherstation.server.request

import jakarta.servlet.http.HttpServletRequest
import org.voegtle.weatherstation.server.util.DateUtil

class ImageUrlParameter(request: HttpServletRequest, dateUtil: DateUtil) : AbstractUrlParameter(request, dateUtil) {
  private val PARAM_OID = "oid"
  private val PARAM_ZX = "zx"
  private val PARAM_FORMAT = "format"
  private val PARAM_SHEET = "sheet"
  private val PARAM_CLEAR = "clear"
  private val PARAM_REFRESH = "refresh"
  private val PARAM_BEGIN = "begin"
  private val PARAM_END = "end"

  val oid: String? = getUrlParameter(PARAM_OID)
  val zx: String? = getUrlParameter(PARAM_ZX)
  val format: String? = getUrlParameter(PARAM_FORMAT)
  val isRefresh: Boolean = getUrlParameterBoolean(PARAM_REFRESH)
  val sheet: Int? = getUrlParameterInteger(PARAM_SHEET, 0)
  val isClear: Boolean = getUrlParameterBoolean(PARAM_CLEAR)
  val begin: Int? = getUrlParameterInteger(PARAM_BEGIN)
  val end: Int? = getUrlParameterInteger(PARAM_END)
}

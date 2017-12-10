package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.util.DateUtil
import org.voegtle.weatherstation.server.util.StringUtil
import java.util.ArrayList
import java.util.Collections
import java.util.TimeZone
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest

class CentralUrlParameter(request: HttpServletRequest) : UrlParameter(request, DateUtil(TimeZone.getDefault()),
                                                                      DataType.CURRENT) {
  private val PARAM_UTF8 = "utf8"
  private val PARAM_BUILD = "build"
  private val PARAM_LOCATIONS = "locations"

  val buildNumber: String? = getUrlParameter(PARAM_BUILD)
  val isUtf8: Boolean = getUrlParameterBoolean(PARAM_UTF8) || StringUtil.isNotEmpty(buildNumber)
  val locations: List<String>

  init {
    this.isNewFormat =  this.isNewFormat || StringUtil.isNotEmpty(buildNumber)

    val locationsStr = getUrlParameter(PARAM_LOCATIONS)
    locations = ArrayList()
    Collections.addAll(locations, *locationsStr!!.split(
        Pattern.quote(",").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

  }

}

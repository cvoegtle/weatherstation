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

  val isUtf8: Boolean
  val buildNumber: String?
  val locations: List<String>

  init {
    this.buildNumber = getUrlParameter(PARAM_BUILD)
    this.isUtf8 = getUrlParameterBoolean(PARAM_UTF8) || StringUtil.isNotEmpty(buildNumber)
    this.isNewFormat =  this.isNewFormat || StringUtil.isNotEmpty(buildNumber)

    val locationsStr = getUrlParameter(PARAM_LOCATIONS)
    locations = ArrayList()
    Collections.addAll(locations, *locationsStr!!.split(
        Pattern.quote(",").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

  }

  companion object {
    private val PARAM_UTF8 = "utf8"
    private val PARAM_BUILD = "build"
    private val PARAM_LOCATIONS = "locations"
  }
}

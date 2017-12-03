package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.persistence.entities.WeatherLocation
import org.voegtle.weatherstation.server.util.StringUtil

import java.net.MalformedURLException
import java.net.URL

class WeatherUrl @Throws(MalformedURLException::class) constructor(location: WeatherLocation, param: UrlParameter) {
  val forwardSecret = StringUtil.isNotEmpty(param.secret) && location.isForwardSecret

  val url: URL = URL("https://" + location.host + "/weatherstation/query?type=" + param.type
                         + (if (param.isExtended) "&ext" else "") + (if (param.isNewFormat) "&new" else "")
                         + if (forwardSecret) "&secret=" + StringUtil.urlEncode(param.secret!!) else "")

  override fun toString(): String = url.toString()
}

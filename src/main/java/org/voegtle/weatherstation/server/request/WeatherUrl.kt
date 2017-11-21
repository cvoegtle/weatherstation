package org.voegtle.weatherstation.server.request

import org.voegtle.weatherstation.server.persistence.entities.WeatherLocation
import org.voegtle.weatherstation.server.util.StringUtil

import java.net.MalformedURLException
import java.net.URL

class WeatherUrl @Throws(MalformedURLException::class) constructor(location: WeatherLocation, param: UrlParameter) {
  var url: URL
    private set

  override fun toString(): String {
    return url.toString()
  }

  init {
    val forwardSecret = StringUtil.isNotEmpty(param.secret) && location.isForwardSecret
    url = URL("https://" + location.host + "/weatherstation/query?type=" + param.type
                  + (if (param.isExtended) "&ext" else "") + (if (param.isNewFormat) "&new" else "")
                  + if (forwardSecret) "&secret=" + StringUtil.urlEncode(param.secret) else "")
  }
}

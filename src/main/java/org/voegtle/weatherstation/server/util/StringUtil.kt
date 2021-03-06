package org.voegtle.weatherstation.server.util


import java.net.URLEncoder

object StringUtil {
  fun isNotEmpty(str: String?): Boolean {
    return str != null && "" != str
  }

  fun isEmpty(str: String?): Boolean {
    return str == null || "" == str
  }

  fun urlEncode(str: String): String = URLEncoder.encode(str, "UTF-8")
}

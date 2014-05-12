package org.voegtle.weatherstation.server.util;


public class StringUtil {
  public static boolean isNotEmpty(String str) {
    return str != null && !"".equals(str);
  }

  public static boolean isEmpty(String str) {
    return str == null || "".equals(str);
  }
}

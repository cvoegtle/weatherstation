package org.voegtle.weatherstation.server.util;

public class MathUtil {
  public static boolean moreOrLessEqual(Float f1, Float f2) {
    if (f1 != null) {
      if (f2 != null) {
        return Math.abs(f1 - f2) < 0.001;
      } else {
        return false;
      }
    }  else {
      return f2 == null;
    }
  }
}

package org.voegtle.weatherstation.server.weewx

class MaxFloat {
  var value: Float? = 0.0f
    set(newMax) {
      this.count++
      if (newMax != null && newMax > value!!) {
        field = newMax
      }
    }

  var count = 0
    private set
}

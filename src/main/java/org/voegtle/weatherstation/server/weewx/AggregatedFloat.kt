package org.voegtle.weatherstation.server.weewx

class AggregatedFloat() {
  var value: Float = 0.0f
    private set
    get() {return if (counter == 0) value else value / counter}

  var counter: Int = 0
    private set

  fun add(add: Int?) {
    if (add != null) {
      this.value += add
      this.counter++
    }

  }

  fun add(add: Float?) {
    if (add != null) {
      this.value += add
      this.counter++
    }
  }
}

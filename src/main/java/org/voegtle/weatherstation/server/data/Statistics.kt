package org.voegtle.weatherstation.server.data

class Statistics {

  var rainLastHour: Float? = null
  val today = StatisticsSet()
  val yesterday = StatisticsSet()
  val last7days = StatisticsSet()
  val last30days = StatisticsSet()

  enum class TimeRange {
    lastHour, today, yesterday, last7days, last30days;


    companion object {

      fun byDay(day: Int): TimeRange = when {
        day == 0 -> today
        day == 1 -> yesterday
        day <= 7 -> last7days
        else -> last30days
      }
    }
  }

  fun setTemperature(range: TimeRange, temperature: Float?) {
    when (range) {
      TimeRange.today -> {
        today.setTemperature(temperature)
        last7days.setTemperature(temperature)
      }

      TimeRange.yesterday -> {
        yesterday.setTemperature(temperature)
        last7days.setTemperature(temperature)
      }

      TimeRange.last7days -> last7days.setTemperature(temperature)

      else -> {
      }
    }
    last30days.setTemperature(temperature)

  }

  fun addRain(range: TimeRange, rain: Float?) {
    when (range) {
      TimeRange.today -> {
        today.addRain(rain)
        last7days.addRain(rain)
      }
      TimeRange.yesterday -> {
        yesterday.addRain(rain)
        last7days.addRain(rain)
      }
      TimeRange.last7days -> last7days.addRain(rain)

      else -> {
      }
    }
    last30days.addRain(rain)
  }

  fun toRainDTO(): RainDTO {
    return RainDTO(lastHour = rainLastHour, today = today.rain, yesterday = yesterday.rain,
                   lastWeek = last7days.rain, last30Days = last30days.rain)
  }

}

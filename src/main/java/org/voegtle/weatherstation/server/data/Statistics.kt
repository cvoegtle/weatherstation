package org.voegtle.weatherstation.server.data

class Statistics {
  enum class Kind {
    standard, withSolarRadiation, withSolarPower
  }

  var kind: Kind = Kind.standard
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
      Statistics.TimeRange.today -> {
        today.setTemperature(temperature)
        last7days.setTemperature(temperature)
      }

      Statistics.TimeRange.yesterday -> {
        yesterday.setTemperature(temperature)
        last7days.setTemperature(temperature)
      }

      Statistics.TimeRange.last7days -> last7days.setTemperature(temperature)

      else -> {
      }
    }
    last30days.setTemperature(temperature)

  }

  fun addRain(range: TimeRange, rain: Float?) {
    when (range) {
      Statistics.TimeRange.today -> {
        today.addRain(rain)
        last7days.addRain(rain)
      }
      Statistics.TimeRange.yesterday -> {
        yesterday.addRain(rain)
        last7days.addRain(rain)
      }
      Statistics.TimeRange.last7days -> last7days.addRain(rain)

      else -> {
      }
    }
    last30days.addRain(rain)
  }

  fun addKwh(range: TimeRange, doubleKwh: Float?) {
    if (doubleKwh != null) {
      val kwh = doubleKwh.toFloat()
      when (range) {
        Statistics.TimeRange.today -> {
          today.addKwh(kwh)
          last7days.addKwh(kwh)
        }
        Statistics.TimeRange.yesterday -> {
          yesterday.addKwh(kwh)
          last7days.addKwh(kwh)
        }
        Statistics.TimeRange.last7days -> last7days.addKwh(kwh)

        else -> {
        }
      }
      last30days.addKwh(kwh)
    }
  }

  fun updateSolarRadiation(range: TimeRange, solarRadiation: Float) {
    when (range) {
      TimeRange.today -> {
        today.updateSolarRadiation(solarRadiation)
        last7days.updateSolarRadiation(solarRadiation)
      }
      TimeRange.yesterday -> {
        yesterday.updateSolarRadiation(solarRadiation)
        last7days.updateSolarRadiation(solarRadiation)
      }
      TimeRange.last7days -> {
        last7days.updateSolarRadiation(solarRadiation)
      }
      else -> {}
    }
    last30days.updateSolarRadiation(solarRadiation)
  }


  fun toRainDTO(): RainDTO {
    return RainDTO(lastHour = rainLastHour, today = today.rain, yesterday = yesterday.rain,
                   lastWeek = last7days.rain, last30Days = last30days.rain)
  }

}

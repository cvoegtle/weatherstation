package org.voegtle.weatherstation.server.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtil(val timezone: TimeZone) {

  fun yesterday(): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)
    cal.time = fromGMTtoLocal(cal.time)
    removeTimeFraction(cal)

    cal.add(Calendar.DAY_OF_MONTH, -1)

    return cal.time
  }

  fun today(): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)
    cal.time = fromGMTtoLocal(cal.time)
    removeTimeFraction(cal)

    return cal.time
  }

  fun fromCESTtoGMT(date: Date): Date {
    val cal = Calendar.getInstance(Locale.UK)
    cal.time = date
    cal.add(Calendar.HOUR_OF_DAY, -getTimeOffset(date, tzCEST))
    return cal.time
  }

  fun fromGMTtoCEST(date: Date): Date {
    val cal = Calendar.getInstance(tzCEST, Locale.GERMANY)
    cal.time = date
    cal.add(Calendar.HOUR_OF_DAY, getTimeOffset(date, tzCEST))
    return cal.time
  }

  fun fromLocalToGMT(date: Date?): Date? {
    if (date != null) {
      val cal = Calendar.getInstance(Locale.UK)
      cal.time = date
      cal.add(Calendar.HOUR_OF_DAY, -getTimeOffset(date, timezone))
      return cal.time
    }
    return null
  }

  fun fromGMTtoLocal(date: Date): Date {
    val cal = Calendar.getInstance(timezone)
    cal.time = date
    cal.add(Calendar.HOUR_OF_DAY, getTimeOffset(date, timezone))
    return cal.time
  }

  fun getRangeAround(date: Date, rangeInSeconds: Int): TimeRange {
    val cal = Calendar.getInstance(Locale.GERMANY)
    cal.time = date
    cal.add(Calendar.SECOND, -rangeInSeconds)
    val start = cal.time

    cal.time = date
    cal.add(Calendar.SECOND, rangeInSeconds)
    val end = cal.time

    return TimeRange(start, end)
  }

  fun isClearlyBefore(date: Date, compareWith: Date): Boolean {
    val cal = Calendar.getInstance(Locale.GERMANY)
    cal.time = date
    cal.add(Calendar.DAY_OF_YEAR, 1)
    cal.add(Calendar.MINUTE, 15)

    return cal.time.before(compareWith)
  }

  /**
   * @param year  year YYYY
   * @param month 1 -12
   * @param day   1 -31
   * @return date as object
   */
  fun getDate(year: Int, month: Int, day: Int): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)
    removeTimeFraction(cal)

    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month - 1)
    cal.set(Calendar.DAY_OF_MONTH, day)

    return cal.time
  }

  private fun removeTimeFraction(cal: Calendar) {
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
  }

  fun incrementDay(previousDay: Date): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)

    cal.time = previousDay
    cal.add(Calendar.DAY_OF_MONTH, 1)

    return cal.time
  }

  fun incrementHour(start: Date): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)

    cal.time = start
    cal.add(Calendar.HOUR_OF_DAY, 1)

    return cal.time
  }

  fun incrementDateBy15min(currentTime: Date): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)
    cal.time = currentTime
    cal.add(Calendar.MINUTE, 15)

    return cal.time
  }

  fun nextDay(date: Date): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)

    cal.time = date
    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 3)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    return cal.time
  }

  fun toLocalTime(date: Date): String {
    val sdf = SimpleDateFormat("HH:mm")

    sdf.timeZone = timezone
    return sdf.format(date)
  }

  fun toLocalDate(date: Date): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    sdf.timeZone = timezone
    return sdf.format(date)
  }

  fun formatAsDate(day: Date): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    sdf.timeZone = timezone
    return sdf.format(day)
  }

  private fun getTimeOffset(date: Date, tz: TimeZone): Int {
    return tz.getOffset(date.time) / 3600000
  }


  fun daysEarlier(date: Date, daysBefore: Int): Date {
    val cal = Calendar.getInstance(Locale.GERMANY)

    cal.time = date
    cal.add(Calendar.DAY_OF_YEAR, -daysBefore)

    return cal.time
  }

  val tzCEST = TimeZone.getTimeZone("Europe/Berlin")

  companion object {
    fun minutesBefore(referenceDate: Date?, minutes: Int): Calendar {
      val timeBefore = Calendar.getInstance()
      if (referenceDate != null) {
        timeBefore.time = referenceDate
      }

      timeBefore.add(Calendar.MINUTE, -Math.abs(minutes))
      return timeBefore
    }
  }
}

fun parseUtcDate(timeStr: String): Date {
  val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  sdf.timeZone = TimeZone.getTimeZone("UTC")
  return sdf.parse(timeStr)
}


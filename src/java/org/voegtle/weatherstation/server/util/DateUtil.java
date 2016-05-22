package org.voegtle.weatherstation.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

public class DateUtil {
  private static final TimeZone timezone = TimeZone.getTimeZone("Europe/Berlin");
  private static final Logger log = Logger.getLogger(DateUtil.class.getName());


  public static Date fromCESTtoGMT(Date date) {
    Calendar cal = Calendar.getInstance(Locale.UK);
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, -getTimeOffset(date));
    return cal.getTime();
  }

  public static Date fromGMTtoCEST(Date date) {
    Calendar cal = Calendar.getInstance(timezone, Locale.GERMANY);
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, getTimeOffset(date));
    return cal.getTime();
  }

  public static TimeRange getRangeAround(Date date, int rangeInSeconds) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(date);
    cal.add(Calendar.SECOND, -rangeInSeconds);
    Date start = cal.getTime();

    cal.setTime(date);
    cal.add(Calendar.SECOND, rangeInSeconds);
    Date end = cal.getTime();

    return new TimeRange(start, end);
  }

  public static Date getYesterday() {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(fromGMTtoCEST(cal.getTime()));
    removeTimeFraction(cal);

    cal.add(Calendar.DAY_OF_MONTH, -1);

    return cal.getTime();
  }

  public static Date getToday() {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(fromGMTtoCEST(cal.getTime()));
    removeTimeFraction(cal);

    return cal.getTime();
  }

  public static boolean isClearlyBefore(Date date, Date compareWith) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_YEAR, 1);
    cal.add(Calendar.MINUTE, 15);

    return cal.getTime().before(compareWith);
  }

  /**
   * @param year  year YYYY
   * @param month 1 -12
   * @param day   1 -31
   * @return date as object
   */
  public static Date getDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    removeTimeFraction(cal);

    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, day);

    return cal.getTime();
  }

  private static void removeTimeFraction(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
  }

  public static Date incrementDay(Date previousDay) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(previousDay);
    cal.add(Calendar.DAY_OF_MONTH, 1);

    return cal.getTime();
  }

  public static Date incrementHour(Date start) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(start);
    cal.add(Calendar.HOUR_OF_DAY, 1);

    return cal.getTime();
  }

  public static Date nextDay(Date date) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(date);
    cal.add(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 3);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  public static String toLocalTime(Date date, String timezone) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    sdf.setTimeZone(TimeZone.getTimeZone(timezone));
    return sdf.format(date);
  }


  private static int getTimeOffset(Date date) {
    return timezone.getOffset(date.getTime()) / 3600000;
  }


  public static Date daysEarlier(Date date, int daysBefore) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(date);
    cal.add(Calendar.DAY_OF_YEAR, -daysBefore);

    return cal.getTime();
  }

  public static Date toDate(Date timestamp) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(timestamp);
    removeTimeFraction(cal);
    return cal.getTime();
  }
}

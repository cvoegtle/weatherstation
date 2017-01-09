package org.voegtle.weatherstation.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

  private final TimeZone timezone;
  private static final TimeZone tzCEST = TimeZone.getTimeZone("Europe/Berlin");

  public DateUtil(TimeZone timezone) {
    this.timezone = timezone;
  }

  public Date fromCESTtoGMT(Date date) {
    Calendar cal = Calendar.getInstance(Locale.UK);
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, -getTimeOffset(date, tzCEST));
    return cal.getTime();
  }

  public Date fromGMTtoCEST(Date date) {
    Calendar cal = Calendar.getInstance(tzCEST, Locale.GERMANY);
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, getTimeOffset(date, tzCEST));
    return cal.getTime();
  }

  public Date fromLocalToGMT(Date date) {
    Calendar cal = Calendar.getInstance(Locale.UK);
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, -getTimeOffset(date, timezone));
    return cal.getTime();
  }

  public Date fromGMTtoLocal(Date date){
    Calendar cal = Calendar.getInstance(timezone);
    cal.setTime(date);
    cal.add(Calendar.HOUR_OF_DAY, getTimeOffset(date, timezone));
    return cal.getTime();
  }

  public TimeRange getRangeAround(Date date, int rangeInSeconds) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(date);
    cal.add(Calendar.SECOND, -rangeInSeconds);
    Date start = cal.getTime();

    cal.setTime(date);
    cal.add(Calendar.SECOND, rangeInSeconds);
    Date end = cal.getTime();

    return new TimeRange(start, end);
  }

  public Date getYesterday() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(fromGMTtoLocal(cal.getTime()));
    removeTimeFraction(cal);

    cal.add(Calendar.DAY_OF_MONTH, -1);

    return cal.getTime();
  }

  public Date getToday() {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(fromGMTtoLocal(cal.getTime()));
    removeTimeFraction(cal);

    return cal.getTime();
  }

  public boolean isClearlyBefore(Date date, Date compareWith) {
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
  public Date getDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    removeTimeFraction(cal);

    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, day);

    return cal.getTime();
  }

  private void removeTimeFraction(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
  }

  public Date incrementDay(Date previousDay) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(previousDay);
    cal.add(Calendar.DAY_OF_MONTH, 1);

    return cal.getTime();
  }

  public Date incrementHour(Date start) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(start);
    cal.add(Calendar.HOUR_OF_DAY, 1);

    return cal.getTime();
  }

  public Date nextDay(Date date) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(date);
    cal.add(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 3);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  public String toLocalTime(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    sdf.setTimeZone(timezone);
    return sdf.format(date);
  }

  public String toLocalDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(timezone);
    return sdf.format(date);
  }

  public String formatAsDate(Date day) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    sdf.setTimeZone(timezone);
    return sdf.format(day);
  }



  private int getTimeOffset(Date date, TimeZone tz) {
    return tz.getOffset(date.getTime()) / 3600000;
  }


  public Date daysEarlier(Date date, int daysBefore) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    cal.setTime(date);
    cal.add(Calendar.DAY_OF_YEAR, -daysBefore);

    return cal.getTime();
  }

  public static Calendar minutesBefore(Date referenceDate, int minutes) {
    Calendar timeBefore = Calendar.getInstance();
    if (referenceDate != null) {
      timeBefore.setTime(referenceDate);
    }

    timeBefore.add(Calendar.MINUTE, -(Math.abs(minutes)));
    return timeBefore;
  }

}

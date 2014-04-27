package org.voegtle.weatherstation.server.util;

import java.util.Date;

public class TimeRange {
  private final Date begin;
  private final Date end;

  TimeRange(Date begin, Date end) {
    this.begin = begin;
    this.end = end;
  }

  public Date getBegin() {
    return begin;
  }

  public Date getEnd() {
    return end;
  }

}

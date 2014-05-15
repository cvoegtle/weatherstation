package org.voegtle.weatherstation.server.data;

public class RainDTO {
  private Float lastHour;
  private Float today;
  private Float yesterday;
  private Float lastWeek;
  private Float last30Days;

  public RainDTO() {
  }

  public Float getLastHour() {
    return lastHour;
  }

  public void setLastHour(Float lastHour) {
    this.lastHour = lastHour;
  }

  public Float getToday() {
    return today;
  }

  public void setToday(Float today) {
    this.today = today;
  }

  public Float getYesterday() {
    return yesterday;
  }

  public void setYesterday(Float yesterday) {
    this.yesterday = yesterday;
  }

  public Float getLastWeek() {
    return lastWeek;
  }

  public void setLastWeek(Float lastWeek) {
    this.lastWeek = lastWeek;
  }

  public Float getLast30Days() {
    return last30Days;
  }

  public void setLast30Days(Float last30Days) {
    this.last30Days = last30Days;
  }
}

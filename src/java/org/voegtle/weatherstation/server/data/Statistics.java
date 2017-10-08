package org.voegtle.weatherstation.server.data;

public class Statistics {
  public enum TimeRange {
    lastHour, today, yesterday, last7days, last30days;

    public static TimeRange byDay(int day) {
      TimeRange range;
      if (day == 0) {
        range = today;
      } else if (day == 1) {
        range = yesterday;
      } else if (day <= 7) {
        range = last7days;
      } else {
        range = last30days;
      }
      return range;
    }
  }

  private Float rainLastHour;
  private StatisticsSet today = new StatisticsSet();
  private StatisticsSet yesterday = new StatisticsSet();
  private StatisticsSet last7days = new StatisticsSet();
  private StatisticsSet last30days = new StatisticsSet();

  public Statistics() {
  }

  public void setTemperature(TimeRange range, Float temperature) {
    switch (range) {
      case today:
        today.setTemperature(temperature);
        last7days.setTemperature(temperature);
        break;
      case yesterday:
        yesterday.setTemperature(temperature);
        last7days.setTemperature(temperature);
        break;
      case last7days:
        last7days.setTemperature(temperature);
        break;
    }
    last30days.setTemperature(temperature);

  }

  public void setRainLastHour(Float rain) {
    this.rainLastHour = rain;
  }

  public void addRain(TimeRange range, Float rain) {
    switch (range) {
      case today:
        today.addRain(rain);
        last7days.addRain(rain);
        break;
      case yesterday:
        yesterday.addRain(rain);
        last7days.addRain(rain);
        break;
      case last7days:
        last7days.addRain(rain);
        break;
    }
    last30days.addRain(rain);
  }

  public void addKwh(TimeRange range, Double doubleKwh) {
    if (doubleKwh != null) {
      Float kwh = new Float(doubleKwh);
      switch (range) {
        case today:
          today.addKwh(kwh);
          last7days.addKwh(kwh);
          break;
        case yesterday:
          yesterday.addKwh(kwh);
          last7days.addKwh(kwh);
          break;
        case last7days:
          last7days.addKwh(kwh);
          break;
      }
      last30days.addKwh(kwh);
    }
  }

  public StatisticsSet getToday() {
    return today;
  }

  public StatisticsSet getYesterday() {
    return yesterday;
  }

  public StatisticsSet getLast7days() {
    return last7days;
  }

  public StatisticsSet getLast30days() {
    return last30days;
  }

  public RainDTO toRainDTO() {
    RainDTO rain = new RainDTO();
    rain.setLastHour(rainLastHour);
    rain.setToday(today.getRain());
    rain.setYesterday(yesterday.getRain());
    rain.setLastWeek(last7days.getRain());
    rain.setLast30Days(last30days.getRain());
    return rain;
  }


  public Float getRainLastHour() {
    return rainLastHour;
  }

}

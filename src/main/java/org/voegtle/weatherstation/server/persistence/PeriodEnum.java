package org.voegtle.weatherstation.server.persistence;

public enum PeriodEnum {
  DAY("day"), WEEK("week"), MONTH("month"), YEAR("year");

  private final String name;

  private PeriodEnum(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}

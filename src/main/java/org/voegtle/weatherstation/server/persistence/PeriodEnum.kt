package org.voegtle.weatherstation.server.persistence

enum class PeriodEnum constructor(val identifier: String) {
  DAY("day"), WEEK("week"), MONTH("month"), YEAR("year");

  override fun toString(): String = identifier
}

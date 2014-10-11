package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.persistence.*;
import org.voegtle.weatherstation.server.util.DateUtil;

import java.util.Date;
import java.util.List;

public class WeatherDataAggregator {
  private final PersistenceManager pm;

  public WeatherDataAggregator(PersistenceManager pm) {
    this.pm = pm;
  }

  public void aggregateWeatherData() {
    Date dateOfLastAggregation = fetchDateOfLastAggregation();
    Date dateOfLastWeatherDataSet = fetchLastDateWithCompleteWeatherDataSets();

    while (DateUtil.isClearlyBefore(dateOfLastAggregation, dateOfLastWeatherDataSet)) {
      AggregatedWeatherDataSet aggregatedDay = createNewDay(dateOfLastAggregation);
      List<SmoothedWeatherDataSet> weatherDataSets = pm.fetchSmoothedWeatherDataInRange(DateUtil.fromCESTtoGMT(aggregatedDay.getDate()),
          DateUtil.fromCESTtoGMT(DateUtil.nextDay(aggregatedDay.getDate())));
      aggregate(aggregatedDay, weatherDataSets);

      pm.makePersitant(aggregatedDay);
      dateOfLastAggregation = aggregatedDay.getDate();
    }
  }

  private void aggregate(AggregatedWeatherDataSet aggregation, List<SmoothedWeatherDataSet> weatherDataSets) {
    if (weatherDataSets.size() > 0) {
      Integer rainCountStart = null;
      Integer rainCountLast = null;
      for (SmoothedWeatherDataSet wds : weatherDataSets) {
        if (wds.isValid()) {
          aggregation.addOutsideTemperature(wds.getOutsideTemperature(), wds.getTimestamp());
          aggregation.addOutsideHumidity(wds.getOutsideHumidity());
          aggregation.addInsideTemperature(wds.getInsideTemperature());
          aggregation.addInsideHumidity(wds.getInsideHumidity());
          if (rainCountStart == null) {
            rainCountStart = wds.getRainCounter();
          }
          rainCountLast = wds.getRainCounter();
        }
      }

      if (rainCountStart != null) {
        aggregation.setRainCounter(Math.max(rainCountLast - rainCountStart, 0));
        aggregation.setRainDays(rainCountLast > rainCountStart ? 1 : 0);
      }

      aggregation.normalize();
    }
    aggregation.setFinished(true);
  }

  private Date fetchDateOfLastAggregation() {
    AggregatedWeatherDataSet lastAggregatedDay = pm.fetchYoungestAggregatedDataSet(PeriodEnum.DAY);

    return lastAggregatedDay == null ? DateUtil.getDate(2014, 9, 24) : lastAggregatedDay.getDate();
  }

  private Date fetchLastDateWithCompleteWeatherDataSets() {
    WeatherDataSet youngest = pm.fetchYoungestDataSet();
    Date timestamp = DateUtil.daysEarlier(youngest.getTimestamp(), 1);
    return DateUtil.toDate(timestamp);
  }

  private AggregatedWeatherDataSet createNewDay(Date lastDay) {
    return new AggregatedWeatherDataSet(DateUtil.incrementDay(lastDay), PeriodEnum.DAY);
  }
}

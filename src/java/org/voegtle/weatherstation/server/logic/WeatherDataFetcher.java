package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.data.RainDTO;
import org.voegtle.weatherstation.server.data.Statistics;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.persistence.*;
import org.voegtle.weatherstation.server.util.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class WeatherDataFetcher {
  private static final Logger log = Logger.getLogger(WeatherDataFetcher.class.getName());

  private final PersistenceManager pm;
  private final DateUtil dateUtil;
  private final LocationProperties locationProperties;

  public WeatherDataFetcher(PersistenceManager pm, LocationProperties locationProperties) {
    this.pm = pm;
    this.locationProperties = locationProperties;
    this.dateUtil = locationProperties.getDateUtil();
  }

  public List<AggregatedWeatherDataSet> getAggregatedWeatherData(Date begin, Date end) {
    return pm.fetchAggregatedWeatherDataInRange(begin, end);
  }

  public List<SmoothedWeatherDataSet> fetchSmoothedWeatherData(Date begin, Date end) {
    return pm.fetchSmoothedWeatherDataInRange(begin, end);
  }

  public List<SmoothedWeatherDataSet> fetchTodaysDataSets() {
    Date today = dateUtil.getToday();
    return pm.fetchSmoothedWeatherDataInRange(dateUtil.fromLocalToGMT(today), null);
  }

  public SmoothedWeatherDataSet getFirstDataSetOfToday() {
    Date today = dateUtil.getToday();
    today = dateUtil.fromLocalToGMT(today);
    Date oneHourLater = dateUtil.incrementHour(today);
    return pm.fetchOldestSmoothedDataSetInRange(today, oneHourLater);
  }

  public UnformattedWeatherDTO getLatestWeatherDataUnformatted(boolean authorized) {
    SmoothedWeatherDataSet today = getFirstDataSetOfToday();
    WeatherDataSet latest = pm.fetchYoungestDataSet();
    SmoothedWeatherDataSet fifteenMinutesBefore = pm.fetchDataSetMinutesBefore(latest.getTimestamp(), 15);
    SmoothedWeatherDataSet oneHourBefore = pm.fetchDataSetMinutesBefore(latest.getTimestamp(), 60);

    UnformattedWeatherDTO dto = new UnformattedWeatherDTO();
    dto.setTime(latest.getTimestamp());
    dto.setLocalTime(dateUtil.toLocalTime(latest.getTimestamp()));
    dto.setTemperature(latest.getOutsideTemperature());
    dto.setHumidity(latest.getOutsideHumidity());

    boolean raining = isRaining(latest, fifteenMinutesBefore);
    dto.setRaining(raining);

    if (locationProperties.isWindRelevant()) {
      dto.setWindspeed(latest.getWindspeed());
    }
    dto.setWatt(latest.getWatt());
    if (authorized) {
      dto.setInsideTemperature(latest.getInsideTemperature());
      dto.setInsideHumidity(latest.getInsideHumidity());
    }

    if (oneHourBefore != null && oneHourBefore.getRainCounter() != null) {
      dto.setRainLastHour(calculateRain(latest.getRainCounter(), oneHourBefore.getRainCounter()));
    }

    if (today != null && today.getRainCounter() != null) {
      dto.setRainToday(calculateRain(latest.getRainCounter(), today.getRainCounter()));
    }

    return dto;
  }

  private boolean isRaining(WeatherDataSet latest, SmoothedWeatherDataSet fifteenMinutesBefore) {
    boolean raining = false;
    if (latest.isRaining() != null) {
      raining = latest.isRaining();
    }
    if (latest.getRainCounter() != null && fifteenMinutesBefore.getRainCounter() != null) {
      raining = raining || (latest.getRainCounter() - fifteenMinutesBefore.getRainCounter()) > 0;
    }
    return raining;
  }

  public Statistics fetchStatistics() {
    Statistics stats = new Statistics();
    buildHistoricStatistics(stats);
    buildTodaysStatistics(stats);
    return stats;
  }

  private void buildHistoricStatistics(Statistics stats) {
    Date yesterday = dateUtil.getYesterday();
    log.warning("Yesterday: " + yesterday);
    List<AggregatedWeatherDataSet> dataSets = pm.fetchAggregatedWeatherDataInRange(dateUtil.daysEarlier(yesterday, 29), yesterday, false);

    int day = 1;
    for (AggregatedWeatherDataSet dataSet : dataSets) {
      Statistics.TimeRange range = Statistics.TimeRange.byDay(day++);

      Float rain = calculateRain(dataSet.getRainCounter(), 0);
      if (rain != null) {
        stats.addRain(range, rain);
      }

      stats.addKwh(range, calculateKwh(dataSet.getKwh(), 0.0d));

      stats.setTemperature(range, dataSet.getOutsideTemperatureMax());
      stats.setTemperature(range, dataSet.getOutsideTemperatureMin());
    }
  }

  private void buildTodaysStatistics(Statistics stats) {
    List<SmoothedWeatherDataSet> todaysDataSets = fetchTodaysDataSets();

    if (todaysDataSets.size() > 0) {
      SmoothedWeatherDataSet firstSet = todaysDataSets.get(0);
      WeatherDataSet latest = pm.fetchYoungestDataSet();
      SmoothedWeatherDataSet oneHourBefore = pm.fetchDataSetMinutesBefore(latest.getTimestamp(), 60);
      stats.setRainLastHour(calculateRain(latest, oneHourBefore));

      stats.addRain(Statistics.TimeRange.today, calculateRain(latest, firstSet));
      stats.addKwh(Statistics.TimeRange.today, calculateKwh(latest, firstSet));
      stats.setTemperature(Statistics.TimeRange.today, latest.getOutsideTemperature());

      for (SmoothedWeatherDataSet dataSet : todaysDataSets) {
        stats.setTemperature(Statistics.TimeRange.today, dataSet.getOutsideTemperature());
      }
    }
  }


  public RainDTO fetchRainData() {
    Statistics statistics = fetchStatistics();

    return statistics.toRainDTO();
  }

  private Float calculateRain(WeatherDataSet latest, SmoothedWeatherDataSet previous) {
    if (latest == null || previous == null || latest.getRainCounter() == null || previous.getRainCounter() == null) {
      return null;
    }
    return calculateRain(latest.getRainCounter(), previous.getRainCounter());
  }

  private Float calculateRain(int youngerCount, int olderCount) {
    int rainCount = youngerCount - olderCount;
    if (rainCount > 0) {
      return (float) (0.295 * rainCount);
    }
    return null;
  }

  private Double calculateKwh(WeatherDataSet latest, SmoothedWeatherDataSet previous) {
    if (latest == null || previous == null || latest.getKwh() == null || previous.getKwh() == null) {
      return null;
    }
    return calculateKwh(latest.getKwh(), previous.getKwh());
  }

  private Double calculateKwh(Double youngerKwh, Double olderKwh) {
    if (youngerKwh != null && olderKwh != null) {
      double kwh = youngerKwh - olderKwh;
      return kwh > 0 ? kwh : null;
    }
    return null;
  }


}

package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.data.RainDTO;
import org.voegtle.weatherstation.server.data.Statistics;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.util.DateUtil;

import java.util.Date;
import java.util.List;

public class WeatherDataFetcher {
  private final PersistenceManager pm;

  public WeatherDataFetcher(PersistenceManager pm) {
    this.pm = pm;
  }

  public List<AggregatedWeatherDataSet> getAggregatedWeatherData(Date begin, Date end) {
    return pm.fetchAggregatedWeatherDataInRange(begin, end);
  }

  public List<SmoothedWeatherDataSet> fetchSmoothedWeatherData(Date begin, Date end) {
    return pm.fetchSmoothedWeatherDataInRange(begin, end);
  }

  public List<SmoothedWeatherDataSet> fetchTodaysDataSets() {
    Date today = DateUtil.getToday();
    return pm.fetchSmoothedWeatherDataInRange(DateUtil.fromCESTtoGMT(today), null);
  }

  public SmoothedWeatherDataSet getFirstDataSetOfToday() {
    Date today = DateUtil.getToday();
    Date oneHourLater = DateUtil.incrementHour(today);
    return pm.fetchOldestSmoothedDataSetInRange(DateUtil.fromCESTtoGMT(today), DateUtil.fromCESTtoGMT(oneHourLater));
  }

  public UnformattedWeatherDTO getLatestWeatherDataUnformatted(boolean authorized) {
    SmoothedWeatherDataSet today = getFirstDataSetOfToday();
    WeatherDataSet latest = pm.fetchYoungestDataSet();
    SmoothedWeatherDataSet oneHourBefore = pm.fetchDataSetOneHourBefore(latest.getTimestamp());

    UnformattedWeatherDTO dto = new UnformattedWeatherDTO();
    dto.setTime(latest.getTimestamp());
    dto.setTemperature(latest.getOutsideTemperature());
    dto.setHumidity(latest.getOutsideHumidity());
    dto.setRaining(latest.isRaining());
    dto.setWindspeed(latest.getWindSpeed());
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

  public Statistics fetchStatistics() {
    Statistics stats = new Statistics();
    buildHistoricStatistics(stats);
    buildTodaysStatistics(stats);
    return stats;
  }

  private void buildHistoricStatistics(Statistics stats) {
    Date yesterday = DateUtil.getYesterday();
    List<AggregatedWeatherDataSet> dataSets = pm.fetchAggregatedWeatherDataInRange(DateUtil.daysEarlier(yesterday, 29), yesterday, false);

    int day = 1;
    for (AggregatedWeatherDataSet dataSet : dataSets) {
      Statistics.TimeRange range = Statistics.TimeRange.byDay(day++);

      Float rain = calculateRain(dataSet.getRainCounter(), 0);
      if (rain != null) {
        stats.addRain(range, rain);
      }
      stats.setTemperature(range, dataSet.getOutsideTemperatureMax());
      stats.setTemperature(range, dataSet.getOutsideTemperatureMin());
    }
  }

  private void buildTodaysStatistics(Statistics stats) {
    List<SmoothedWeatherDataSet> todaysDataSets = fetchTodaysDataSets();

    if (todaysDataSets.size() > 0) {
      SmoothedWeatherDataSet firstSet = todaysDataSets.get(0);
      WeatherDataSet latest = pm.fetchYoungestDataSet();
      SmoothedWeatherDataSet oneHourBefore = pm.fetchDataSetOneHourBefore(latest.getTimestamp());
      stats.setRainLastHour(calculateRain(latest, oneHourBefore));

      stats.addRain(Statistics.TimeRange.today, calculateRain(latest, firstSet));
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
}

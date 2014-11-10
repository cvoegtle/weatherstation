package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.data.RainDTO;
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
    }

    if (oneHourBefore != null && oneHourBefore.getRainCounter() != null) {
      dto.setRainLastHour(calculateRain(latest.getRainCounter(), oneHourBefore.getRainCounter()));
    }

    if (today != null && today.getRainCounter() != null) {
      dto.setRainToday(calculateRain(latest.getRainCounter(), today.getRainCounter()));
    }

    return dto;
  }

  public RainDTO fetchRainData() {
    RainDTO rainDTO = new RainDTO();

    SmoothedWeatherDataSet today = getFirstDataSetOfToday();
    WeatherDataSet latest = pm.fetchYoungestDataSet();
    SmoothedWeatherDataSet oneHourBefore = pm.fetchDataSetOneHourBefore(latest.getTimestamp());

    rainDTO.setLastHour(calculateRain(latest, oneHourBefore));
    Float rainToday = calculateRain(latest, today);
    rainDTO.setToday(rainToday);

    Date yesterday = DateUtil.getYesterday();
    List<AggregatedWeatherDataSet> dataSets = pm.fetchAggregatedWeatherDataInRange(DateUtil.daysEarlier(yesterday, 29), yesterday, false);
    AggregatedWeatherDataSet yesterdaysData = dataSets.get(0);
    if (yesterdaysData != null) {
      rainDTO.setYesterday(calculateRain(yesterdaysData.getRainCounter(), 0));
    }

    int days = 0;
    int rainCountWeek = 0;
    int rainCount30days = 0;
    for (AggregatedWeatherDataSet ads : dataSets) {
      if (days < 6) {
        rainCountWeek += ads.getRainCounter();
      }
      rainCount30days += ads.getRainCounter();
      days++;
    }

    float rainWeek = (float) (rainCountWeek * 0.295);
    float rain30days = (float) (rainCount30days * 0.295);

    if (rainToday != null) {
      rainWeek += rainToday;
      rain30days += rainToday;
    }

    if (rainWeek > 0.1) {
      rainDTO.setLastWeek(rainWeek);
    }

    if (rain30days > 0.1) {
      rainDTO.setLast30Days(rain30days);
    }

    return rainDTO;
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

package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.util.DateUtil;

import java.util.Date;
import java.util.List;

public class WeatherDataFetcher {
  private PersistenceManager pm;

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

  public UnformattedWeatherDTO getLatestWeatherDataUnformatted() {
    SmoothedWeatherDataSet today = getFirstDataSetOfToday();
    WeatherDataSet latest = pm.fetchYoungestDataSet();
    SmoothedWeatherDataSet oneHourBefore = pm.fetchDataSetOneHourBefore(latest.getTimestamp());

    UnformattedWeatherDTO dto = new UnformattedWeatherDTO();
    dto.setTime(latest.getTimestamp());
    dto.setTemperature(latest.getOutsideTemperature());
    dto.setHumidity(latest.getOutsideHumidity());
    dto.setRaining(latest.isRaining());
    dto.setWindspeed(latest.getWindSpeed());

    if (oneHourBefore != null && oneHourBefore.getRainCounter() != null) {
      Integer rainCount = latest.getRainCounter() - oneHourBefore.getRainCounter();
      if (rainCount > 0) {
        float rainAmount = (float) (0.295 * rainCount);
        dto.setRainLastHour(rainAmount);
      }
    }

    if (today != null && today.getRainCounter() != null) {
      Integer rainCount = latest.getRainCounter() - today.getRainCounter();
      if (rainCount > 0) {
        float rainAmount = (float) (0.295 * rainCount);
        dto.setRainToday(rainAmount);
      }
    }

    return dto;
  }

}

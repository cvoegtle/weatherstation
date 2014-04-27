package org.voegtle.weatherstation.server.logic;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.voegtle.weatherstation.client.dto.UnformattedWeatherDTO;
import org.voegtle.weatherstation.client.dto.WeatherDTO;
import org.voegtle.weatherstation.server.persistence.AggregatedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.util.DateUtil;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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

  public WeatherDTO getLatestWeatherData() {
    WeatherDataSet latest = pm.fetchYoungestDataSet();
    SmoothedWeatherDataSet oneHourBefore = pm.fetchDataSetOneHourBefore(latest.getTimestamp());

    WeatherDTO dto = createWeatherDTO(latest, oneHourBefore);
    return dto;
  }

  private WeatherDTO createWeatherDTO(WeatherDataSet latest, SmoothedWeatherDataSet oneHourBefore) {
    WeatherDTO dto = new WeatherDTO();

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMMM - HH:mm");
    dto.setTime(dateFormat.format(DateUtil.fromGMTtoCEST(latest.getTimestamp())));

    NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMANY);
    numberFormat.setMaximumFractionDigits(1);
    dto.setTemperature(numberFormat.format(latest.getOutsideTemperature()) + "°C");
    dto.setHumidity(numberFormat.format(latest.getOutsideHumidity()) + "%");
    dto.setWindspeed(latest.getWindSpeed() + " km/h");

    if (oneHourBefore != null && oneHourBefore.getRainCounter() != null) {
      Integer rainCount = latest.getRainCounter() - oneHourBefore.getRainCounter();
      if (rainCount > 0) {
        double rainAmount = 0.295 * rainCount;
        dto.setRainLastHour(numberFormat.format(rainAmount) + "l");
      }
    }

    UserService us = UserServiceFactory.getUserService();
    if (us.isUserLoggedIn() && us.isUserAdmin()) {
      if (latest.getInsideTemperature() != null) {
        dto.setInsideTemperature(numberFormat.format(latest.getInsideTemperature()) + "°C");
      }
      if (latest.getInsideHumidity() != null) {
        dto.setInsideHumidity(numberFormat.format(latest.getInsideHumidity()) + "%");
      }
    }

    dto.setRaining(latest.isRaining());
    return dto;
  }

}

package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet;
import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.TimeRange;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherDataSmoother {

  private final PersistenceManager pm;
  private final DateUtil dateUtil;

  public WeatherDataSmoother(PersistenceManager pm, DateUtil dateUtil) {
    this.pm = pm;
    this.dateUtil = dateUtil;
  }

  public void smoothWeatherData() {
    Date endTime = calculateEndTime();

    Date currentTime = calculateStartTime(endTime);
    while (currentTime.before(endTime)) {
      TimeRange range = dateUtil.getRangeAround(currentTime, 7 * 60 + 30);
      List<WeatherDataSet> weatherData = pm.fetchWeatherDataInRange(range.getBegin(), range.getEnd());

      SmoothedWeatherDataSet smoothed = new SmoothedWeatherDataSet(currentTime);
      for (WeatherDataSet wds : weatherData) {
        smoothed.add(wds);
      }
      smoothed.normalize();
      pm.makePersitant(smoothed);
      pm.removeWeatherDataInRange(range.getBegin(), range.getEnd());

      currentTime = incrementDateBy15min(currentTime);
    }
  }

  private Date incrementDateBy15min(Date currentTime) {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(currentTime);
    cal.add(Calendar.MINUTE, 15);

    return cal.getTime();
  }

  private Date calculateStartTime(Date timeOfYoungestWeatherDataSet) {
    SmoothedWeatherDataSet youngest = pm.fetchYoungestSmoothedDataSet();

    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    if (youngest != null) {
      cal.setTime(youngest.getTimestamp());
      cal.add(Calendar.MINUTE, 15);
    } else {
      cal.setTime(timeOfYoungestWeatherDataSet);
      cal.add(Calendar.HOUR_OF_DAY, -1);
      cal.set(Calendar.MINUTE, 0);
    }

    return cal.getTime();
  }

  private Date calculateEndTime() {
    WeatherDataSet youngest = pm.fetchYoungestDataSet();
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.setTime(youngest.getTimestamp());
    cal.add(Calendar.MINUTE, -8);
    return cal.getTime();
  }
}

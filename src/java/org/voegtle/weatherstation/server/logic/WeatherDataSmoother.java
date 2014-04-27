package org.voegtle.weatherstation.server.logic;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.TimeRange;

public class WeatherDataSmoother {

  private PersistenceManager pm;

  public WeatherDataSmoother(PersistenceManager pm) {
    this.pm = pm;
  }

  public void smoothWeatherData() {
    Date startTime = calculateStartTime();
    Date endTime = calculateEndTime();

    Date currentTime = startTime;
    while (currentTime.before(endTime)) {
      TimeRange range = DateUtil.getRangeAround(currentTime, 7 * 60 + 30);
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

  private Date calculateStartTime() {
    SmoothedWeatherDataSet youngest = pm.fetchYoungestSmoothedDataSet();

    Calendar cal = Calendar.getInstance(Locale.GERMANY);

    if (youngest != null) {
      cal.setTime(youngest.getTimestamp());
      cal.add(Calendar.MINUTE, 15);
    } else {
      cal.setTime(new Date());
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

package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.parser.DataLine;
import org.voegtle.weatherstation.server.parser.DataParser;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.request.ResponseCode;
import org.voegtle.weatherstation.server.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherDataImporter {
  private static final Logger log = Logger.getLogger(WeatherDataImporter.class.getName());

  private final PersistenceManager pm;
  private final Date importedUntil;

  public WeatherDataImporter(PersistenceManager pm) {
    this.pm = pm;
    this.importedUntil = getDateOfLastDataSet();
  }

  public String doImport(ArrayList<DataLine> lines) {
    String result;
    try {
      boolean persisted = false;
      DataParser parser = new DataParser();
      List<WeatherDataSet> dataSets = parser.parse(lines);
      log.info("Number of valid datasets: " + dataSets.size());
      for (WeatherDataSet dataSet : dataSets) {
        if (isNotOutdated(dataSet)) {
          if (pm.makePersitant(dataSet)) {
            persisted = true;
          }
        }
      }

      if (persisted) {
        new WeatherDataSmoother(pm).smoothWeatherData();
        new WeatherDataAggregator(pm).aggregateWeatherData();
      }
      result = persisted ? ResponseCode.ACKNOWLEDGE : ResponseCode.IGNORED;
    } catch (ParseException ex) {
      log.log(Level.SEVERE, "parsing failed", ex);
      result = ResponseCode.PARSE_ERROR;
    }
    return result;
  }

  private boolean isNotOutdated(WeatherDataSet dataSet) {
    return importedUntil.before(dataSet.getTimestamp());
  }

  private Date getDateOfLastDataSet() {
    Date lastImport = DateUtil.getDate(2014, 1, 1);

    WeatherDataSet youngestDataSet = pm.fetchYoungestDataSet();
    if (youngestDataSet != null && youngestDataSet.getTimestamp().after(lastImport)) {
      lastImport = youngestDataSet.getTimestamp();
    }

    SmoothedWeatherDataSet youngestSmoothedDS = pm.fetchYoungestSmoothedDataSet();
    if (youngestSmoothedDS != null && youngestSmoothedDS.getTimestamp().after(lastImport)) {
      lastImport = youngestSmoothedDS.getTimestamp();
    }
    return lastImport;
  }

}

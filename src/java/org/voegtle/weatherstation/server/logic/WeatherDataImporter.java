package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.parser.DataLine;
import org.voegtle.weatherstation.server.parser.DataParser;
import org.voegtle.weatherstation.server.persistence.*;
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
  private final DateUtil dateUtil;
  private final Date importedUntil;
  private final DataIndicies dataIndicies;
  private LocationProperties locationProperties;

  public WeatherDataImporter(PersistenceManager pm, LocationProperties locationProperties) {
    this.pm = pm;
    this.locationProperties = locationProperties;
    this.dateUtil = locationProperties.getDateUtil();
    this.importedUntil = getDateOfLastDataSet();
    dataIndicies = locationProperties.getDataIndices();
  }

  public String doImport(ArrayList<DataLine> lines) {
    String result;
    try {
      boolean persisted = false;
      DataParser parser = new DataParser(dateUtil, dataIndicies);
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
        new WeatherDataSmoother(pm, dateUtil).smoothWeatherData();
        new WeatherDataAggregator(pm, dateUtil).aggregateWeatherData();
        new WeatherDataForwarder(pm, locationProperties).forwardLastDataset();
      } else {
        log.warning("no dataset has been persisted");
      }

      result = persisted ? ResponseCode.ACKNOWLEDGE : ResponseCode.IGNORED;
    } catch (ParseException ex) {
      log.log(Level.SEVERE, "parsing failed", ex);
      result = ResponseCode.PARSE_ERROR;
    }
    return result;
  }

  private boolean isNotOutdated(WeatherDataSet dataSet) {
    boolean notOutdated = importedUntil.before(dataSet.getTimestamp());
    if (!notOutdated) {
      log.warning("WeatherDataSet from " + dataSet.getTimestamp() + " is outdated. import until: " + importedUntil);
    }
    return notOutdated;
  }

  private Date getDateOfLastDataSet() {
    Date lastImport = dateUtil.getDate(2016, 1, 1);

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

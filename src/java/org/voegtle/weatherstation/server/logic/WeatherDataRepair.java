package org.voegtle.weatherstation.server.logic;

import com.google.appengine.api.datastore.Key;
import org.voegtle.weatherstation.server.logic.data.RepairJob;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;

import java.util.*;
import java.util.logging.Logger;

public class WeatherDataRepair {
  private static final Logger log = Logger.getLogger(WeatherDataRepair.class.getName());

  private final PersistenceManager pm;
  private LocationProperties locationProperties;
  private List<SmoothedWeatherDataSet> datasets;

  public WeatherDataRepair(PersistenceManager pm, LocationProperties locationProperties) {
    this.pm = pm;
    this.locationProperties = locationProperties;
  }

  public List<SmoothedWeatherDataSet> repair(Date begin, Date end) {
    ArrayList<SmoothedWeatherDataSet> repaired = new ArrayList<>();
    WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(pm, locationProperties);
    datasets = weatherDataFetcher.fetchSmoothedWeatherData(begin, end);

    repaired.addAll(removeDuplicates());

    datasets = weatherDataFetcher.fetchSmoothedWeatherData(begin, end);

    RepairJob next = getNextRepairJob();
    while (next.containsData()) {
      repair(next);
      repaired.addAll(next.getDefectDataSets());
      next = getNextRepairJob();
    }

    return repaired;
  }

  private Collection<SmoothedWeatherDataSet> removeDuplicates() {
    HashMap<Key, SmoothedWeatherDataSet> duplicate = new HashMap<>();
    SmoothedWeatherDataSet previousDataset = null;

    for (SmoothedWeatherDataSet dataset : datasets) {
      if (previousDataset != null) {
        if (dataset.getTimestamp().equals(previousDataset.getTimestamp())) {
          if (!dataset.isValid()) {
            duplicate.put(dataset.getKey(), dataset);
          } else {
            duplicate.put(previousDataset.getKey(), previousDataset);
          }
        }
      }
      previousDataset = dataset;
    }

    for (SmoothedWeatherDataSet dataset : duplicate.values()) {
      pm.removeDataset(dataset);
    }

    return duplicate.values();
  }

  private RepairJob getNextRepairJob() {
    RepairJob repairJob = new RepairJob();
    SmoothedWeatherDataSet previousDataset = null;
    for (SmoothedWeatherDataSet dataset : datasets) {
      if (!dataset.isValid()) {
        repairJob.setFirst(previousDataset);
        repairJob.addDefectDataSet(dataset);
      } else if (repairJob.containsData()) {
        repairJob.setLast(dataset);
        break;
      } else {
        previousDataset = dataset;
      }
    }

    repairJob.calculateStep();
    return repairJob;
  }

  private void repair(RepairJob repairJob) {
    if (repairJob.getFirst() != null) {
      SmoothedWeatherDataSet first = repairJob.getFirst();
      RepairJob.RepairStep step = repairJob.getStep();
      int index = 0;
      for (SmoothedWeatherDataSet ds : repairJob.getDefectDataSets()) {
        index++;
        log.info("repair " + index + " - " + ds.getTimestamp());
        log.info("insideTemperature: " + first.getInsideTemperature() + " " + step.insideTemperature);

        ds.setOutsideHumidity(getNewValue(first.getOutsideHumidity(), index, step.humidity));
        ds.setOutsideTemperature(getNewValue(first.getOutsideTemperature(), index, step.temperature));

        ds.setInsideHumidity(getNewValue(first.getInsideHumidity(), index, step.insideHumidity));
        ds.setInsideTemperature(getNewValue(first.getInsideTemperature(), index, step.insideTemperature));

        ds.setRainCounter(getNewValue(first.getRainCounter(), index, step.rain));
        ds.setKwh(getNewValue(first.getKwh(), index, step.kwh));

        setDefaults(ds);
        pm.updateDataset(ds);
      }
    }
  }

  private void setDefaults(SmoothedWeatherDataSet ds) {
    ds.setRaining(false);
    ds.setWindspeed((float) 0.0);
    ds.setWindspeedMax((float) 0.0);
    ds.setRepaired(true);
  }

  private Integer getNewValue(Integer startValue, int index, double step) {
    double value = startValue + index * step;
    return Math.round((float) value);
  }

  private Float getNewValue(Float startValue, int index, Double step) {
    log.info("startValue=" + startValue + ", step=" + step);
    if (step == null) {
      return null;
    }
    double value = startValue + index * step;
    log.info("value=" + value);
    return new Float(value);
  }

  private Double getNewValue(Double startValue, int index, Double step) {
    if (step == null) {
      return null;
    }
    return startValue + index * step;
  }
}

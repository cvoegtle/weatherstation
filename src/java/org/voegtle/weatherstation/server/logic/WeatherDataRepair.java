package org.voegtle.weatherstation.server.logic;

import java.util.*;

import com.google.appengine.api.datastore.Key;
import org.voegtle.weatherstation.server.logic.data.RepairJob;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;

public class WeatherDataRepair {

  private final PersistenceManager pm;
  private List<SmoothedWeatherDataSet> datasets;

  public WeatherDataRepair(PersistenceManager pm) {
    this.pm = pm;
  }

  public List<SmoothedWeatherDataSet> repair(Date begin, Date end) {
    ArrayList<SmoothedWeatherDataSet> repaired = new ArrayList<SmoothedWeatherDataSet>();
    WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(pm);
    datasets = weatherDataFetcher.fetchSmoothedWeatherData(begin, end);

    repaired.addAll(removeDuplicates());

    datasets = weatherDataFetcher.fetchSmoothedWeatherData(begin, end);

    RepairJob next;
    while (!(next = getNextRepairJob()).isEmpty()) {
      repair(next);
      repaired.addAll(next.getDefectDataSets());
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
      } else if (!repairJob.isEmpty()) {
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
      int index = 0;
      for (SmoothedWeatherDataSet ds : repairJob.getDefectDataSets()) {
        index++;
        ds.setOutsideHumidity(getNewValue(first.getOutsideHumidity(), index, repairJob.getStep().humidity));
        ds.setOutsideTemperature(getNewValue(first.getOutsideTemperature(), index, repairJob.getStep().temperature));
        ds.setRainCounter(getNewValue(first.getRainCounter(), index, repairJob.getStep().rain));
        setDefaults(ds);
        pm.updateDataset(ds);
      }
    }
  }

  private void setDefaults(SmoothedWeatherDataSet ds) {
    ds.setRaining(false);
    ds.setWindspeed((float) 0.0);
    ds.setWindspeedMax((float) 0.0);
  }

  private Integer getNewValue(Integer startValue, int index, double step) {
    double value = startValue + index * step;
    return Math.round((float) value);
  }

  private Float getNewValue(Float startValue, int index, double step) {
    double value = startValue + index * step;
    return new Float(value);
  }
}

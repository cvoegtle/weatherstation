package org.voegtle.weatherstation.server.logic.data;

import java.util.ArrayList;
import java.util.List;

import org.voegtle.weatherstation.server.persistence.SmoothedWeatherDataSet;

public class RepairJob {

  public class RepairStep {
    public double temperature = 0.0;
    public double humidity = 0.0;
    public double rain = 0.0;

    public RepairStep() {
    }
  }

  private SmoothedWeatherDataSet first;
  private SmoothedWeatherDataSet last;
  private List<SmoothedWeatherDataSet> defectDataSets = new ArrayList<SmoothedWeatherDataSet>();
  private RepairStep step = new RepairStep();

  public RepairJob() {
  }

  public SmoothedWeatherDataSet getFirst() {
    return first;
  }

  public void setFirst(SmoothedWeatherDataSet first) {
    this.first = first;
  }

  public SmoothedWeatherDataSet getLast() {
    return last;
  }

  public void setLast(SmoothedWeatherDataSet last) {
    this.last = last;
  }

  public List<SmoothedWeatherDataSet> getDefectDataSets() {
    return defectDataSets;
  }

  public void addDefectDataSet(SmoothedWeatherDataSet dataset) {
    defectDataSets.add(dataset);
  }

  public boolean isEmpty() {
    return defectDataSets.size() == 0;
  }

  public void calculateStep() {
    if (first == null || last == null) {
      return;
    }
    if (defectDataSets.size() == 0) {
      return;
    }

    step.humidity = (last.getOutsideHumidity() - first.getOutsideHumidity()) / (defectDataSets.size() + 1);
    step.temperature = (last.getOutsideTemperature() - first.getOutsideTemperature()) / (defectDataSets.size() + 1);
    step.rain = (last.getRainCounter() - first.getRainCounter()) / (defectDataSets.size() + 1);
  }

  public RepairStep getStep() {
    return step;
  }

}

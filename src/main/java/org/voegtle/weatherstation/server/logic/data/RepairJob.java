package org.voegtle.weatherstation.server.logic.data;

import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet;

import java.util.ArrayList;
import java.util.List;

public class RepairJob {

  public class RepairStep {
    public double temperature = 0.0;
    public double humidity = 0.0;
    public double rain = 0.0;
    public Double insideTemperature;
    public Double insideHumidity;
    public Double kwh;

    public RepairStep() {
    }
  }

  private SmoothedWeatherDataSet first;
  private SmoothedWeatherDataSet last;
  private final List<SmoothedWeatherDataSet> defectDataSets = new ArrayList<>();
  private final RepairStep step = new RepairStep();

  public RepairJob() {
  }

  public SmoothedWeatherDataSet getFirst() {
    return first;
  }

  public void setFirst(SmoothedWeatherDataSet first) {
    this.first = first;
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

  public boolean containsData() {
    return defectDataSets.size() > 0;
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
    if (last.getRainCounter() == null) {
      step.rain = 0;
    } else {
      step.rain = (last.getRainCounter() - first.getRainCounter()) / (defectDataSets.size() + 1);
    }

    step.insideHumidity = spreadEqually(first.getInsideHumidity(), last.getInsideHumidity(), defectDataSets.size());
    step.insideTemperature = spreadEqually(first.getInsideTemperature(), last.getInsideTemperature(), defectDataSets.size());
    step.kwh = spreadEqually(first.getKwh(), last.getKwh(), defectDataSets.size());
  }

  private Double spreadEqually(Double firstValue, Double lastValue, int numberOfSets) {
    if (firstValue == null || lastValue == null) {
      return null;
    }
    return (lastValue - firstValue) / (numberOfSets + 1);
  }

  private Double spreadEqually(Float firstValue, Float lastValue, int numberOfSets) {
    if (firstValue == null || lastValue == null) {
      return null;
    }
    return new Double((lastValue - firstValue) / (numberOfSets + 1));
  }

  public RepairStep getStep() {
    return step;
  }

}

package org.voegtle.weatherstation.server.logic.data

import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet

import java.util.ArrayList

class RepairJob {

  var first: SmoothedWeatherDataSet? = null
  var last: SmoothedWeatherDataSet? = null
  val defectDataSets = ArrayList<SmoothedWeatherDataSet>()
  val step = RepairStep()

  inner class RepairStep {
    var temperature = 0.0
    var humidity = 0.0
    var rain = 0.0
    var insideTemperature: Double? = null
    var insideHumidity: Double? = null
    var kwh: Double? = null
  }

  fun addDefectDataSet(dataset: SmoothedWeatherDataSet) {
    defectDataSets.add(dataset)
  }

  fun containsData(): Boolean {
    return defectDataSets.size > 0
  }

  fun calculateStep() {
    if (first == null || last == null) {
      return
    }
    if (defectDataSets.size == 0) {
      return
    }

    step.humidity = ((last!!.outsideHumidity!! - first!!.outsideHumidity!!) / (defectDataSets.size + 1)).toDouble()
    step.temperature = ((last!!.outsideTemperature!! - first!!.outsideTemperature!!) / (defectDataSets.size + 1)).toDouble()
    if (last!!.rainCounter == null) {
      step.rain = 0.0
    } else {
      step.rain = ((last!!.rainCounter!! - first!!.rainCounter!!) / (defectDataSets.size + 1)).toDouble()
    }

    step.insideHumidity = spreadEqually(first!!.insideHumidity, last!!.insideHumidity, defectDataSets.size)
    step.insideTemperature = spreadEqually(first!!.insideTemperature, last!!.insideTemperature, defectDataSets.size)
    step.kwh = spreadEqually(first!!.kwh, last!!.kwh, defectDataSets.size)
  }

  private fun spreadEqually(firstValue: Double?, lastValue: Double?, numberOfSets: Int): Double? {
    return if (firstValue == null || lastValue == null) {
      null
    } else (lastValue - firstValue) / (numberOfSets + 1)
  }

  private fun spreadEqually(firstValue: Float?, lastValue: Float?, numberOfSets: Int): Double? {
    return if (firstValue == null || lastValue == null) {
      null
    } else ((lastValue - firstValue) / (numberOfSets + 1)).toDouble()
  }

}

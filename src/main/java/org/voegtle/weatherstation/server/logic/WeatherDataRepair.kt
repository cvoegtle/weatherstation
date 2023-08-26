package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.logic.data.RepairJob
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet2
import java.util.*
import java.util.logging.Logger

class WeatherDataRepair(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {
  private var datasets: MutableList<SmoothedWeatherDataSet2> = ArrayList<SmoothedWeatherDataSet2>()

  private fun fetchNextRepairJob(): RepairJob {
      val repairJob = RepairJob()
      var previousDataset: SmoothedWeatherDataSet2? = null
      val iter = datasets.iterator()
      while (iter.hasNext()) {
        val dataset = iter.next()
        if (!dataset.isValid) {
          repairJob.first = previousDataset
          repairJob.addDefectDataSet(dataset)
          iter.remove()
        } else if (repairJob.containsData()) {
          repairJob.last = dataset
          break
        } else {
          previousDataset = dataset
        }
      }

      repairJob.calculateStep()
      return repairJob
    }

  fun repair(begin: Date, end: Date?): List<SmoothedWeatherDataSet2> {
    val repaired = ArrayList<SmoothedWeatherDataSet2>()
    val weatherDataFetcher = WeatherDataFetcher(pm, locationProperties)
    datasets = weatherDataFetcher.fetchSmoothedWeatherData(begin, end)

    repaired.addAll(removeDuplicates())

    datasets = weatherDataFetcher.fetchSmoothedWeatherData(begin, end)

    var next = fetchNextRepairJob()
    while (next.containsData()) {
      repair(next)
      repaired.addAll(next.defectDataSets)
      next = fetchNextRepairJob()
    }

    return repaired
  }

  private fun removeDuplicates(): Collection<SmoothedWeatherDataSet2> {
    val duplicate = HashMap<Key, SmoothedWeatherDataSet2>()
    var previousDataset: SmoothedWeatherDataSet2? = null

    for (dataset in datasets) {
      if (previousDataset != null) {
        if (dataset.timestamp == previousDataset.timestamp) {
          if (!dataset.isValid) {
            duplicate.put(dataset.id!!, dataset)
          } else {
            duplicate.put(previousDataset.id!!, previousDataset)
          }
        }
      }
      previousDataset = dataset
    }

    for (dataset in duplicate.values) {
      pm.removeDataset(dataset)
    }

    return duplicate.values
  }

  private fun repair(repairJob: RepairJob) {
    repairJob.first?.let {
      val step = repairJob.step
      var index = 0
      for (ds in repairJob.defectDataSets) {
        index++
        log.info("repair " + index + " - " + ds.timestamp)
        log.info("insideTemperature: " + it.insideTemperature + " " + step.insideTemperature)

        ds.outsideHumidity = getNewValue(it.outsideHumidity, index, step.humidity)
        ds.outsideTemperature = getNewValue(it.outsideTemperature, index, step.temperature)

        ds.insideHumidity = getNewValue(it.insideHumidity, index, step.insideHumidity)
        ds.insideTemperature = getNewValue(it.insideTemperature, index, step.insideTemperature)

        it.rainCounter?.let { rainCounter -> ds.rainCounter = getNewValue(rainCounter, index, step.rain) }

        it.kwh?.let {
          ds.kwh = getNewValue(it, index, step.kwh)
        }

        setDefaults(ds)
        pm.updateDataset(ds)
      }
    }
  }

  private fun setDefaults(ds: SmoothedWeatherDataSet2) {
    ds.isRaining = false
    ds.windspeed = 0.0.toFloat()
    ds.windspeedMax = 0.0.toFloat()
    ds.repaired = true
  }

  private fun getNewValue(startValue: Int, index: Int, step: Double): Int {
    val value = startValue + index * step
    return Math.round(value).toInt()
  }

  private fun getNewValue(startValue: Float?, index: Int, step: Double?): Float? {
    if (step == null || startValue == null) {
      return null
    }
    val value = startValue + index * step
    return value.toFloat()
  }

  private fun getNewValue(startValue: Double, index: Int, step: Double?): Double? {
    return if (step == null) {
      null
    } else startValue + index * step
  }

  companion object {
    private val log = Logger.getLogger(WeatherDataRepair::class.java.name)
  }
}

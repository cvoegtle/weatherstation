package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import org.voegtle.weatherstation.server.weewx.SolarDataSet
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.util.*

class WeatherDataFetcher(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {

  private val dateUtil: DateUtil = locationProperties.dateUtil
  private var solarRadiationTotal: Float? = null

  fun fetchAggregatedWeatherData(begin: Date, end: Date?): List<AggregatedWeatherDataSet> {
    val aggregatedWeatherDataSets = if (end != null) pm.fetchAggregatedWeatherDataInRange(begin, end) else pm.fetchAggregatedWeatherDataInRange(begin)
    aggregatedWeatherDataSets.forEach { wds -> wds.migrateRainCounter2DailyRain() }
    return aggregatedWeatherDataSets
  }

  fun fetchSmoothedWeatherData(begin: Date, end: Date): MutableList<SmoothedWeatherDataSet> {
    val gmtBegin = dateUtil.fromCESTtoGMT(begin)
    val gmtEnd = dateUtil.fromCESTtoGMT(end)
    val smoothedWeatherDataInRange = pm.fetchSmoothedWeatherDataInRange(gmtBegin, gmtEnd)
    calculateRainPerPeriod(smoothedWeatherDataInRange)
    return smoothedWeatherDataInRange
  }

  private fun calculateRainPerPeriod(datasets: MutableList<SmoothedWeatherDataSet>) {
    var previousRain: Float? = null
    datasets.forEach {
      val currentRain = it.dailyRain ?: 0.0f
      if (previousRain != null && currentRain > previousRain!!) {
        it.dailyRain = currentRain - previousRain!!
      } else {
        it.dailyRain = 0.0f
      }
      previousRain = currentRain
    }
  }

  private fun fetchTodaysDataSets(): List<SmoothedWeatherDataSet> {
    val today = dateUtil.today()
    return pm.fetchSmoothedWeatherDataInRange(dateUtil.fromLocalToGMT(today))
  }

  fun getLatestWeatherDataUnformatted(authorized: Boolean): UnformattedWeatherDTO {
    val latestWeewxData: WeewxDataSet = pm.fetchYoungestDataSet()
    val latestSolarData: SolarDataSet? = pm.fetchCorrespondingSolarDataSet(latestWeewxData.time)

    return UnformattedWeatherDTO(
      time = latestWeewxData.time,
      location = locationProperties.city,
      localtime = dateUtil.toLocalTime(latestWeewxData.time),
      temperature = latestWeewxData.temperature,
      humidity = latestWeewxData.humidity,
      barometer = latestWeewxData.barometer,
      solarradiation = latestWeewxData.solarRadiation,
      UV = latestWeewxData.UV,
      isRaining = isRaining(latestWeewxData),
      windspeed = if (locationProperties.isWindRelevant) latestWeewxData.windSpeed else null,
      windgust = if (locationProperties.isWindRelevant) latestWeewxData.windGust else null,
      insideTemperature = if (authorized) latestWeewxData.indoorTemperature else null,
      insideHumidity = if (authorized) latestWeewxData.indoorHumidity else null,
      rainLastHour = latestWeewxData.rain,
      rainToday = latestWeewxData.dailyRain,
      powerProduction = latestSolarData?.powerProduction,
      powerFeed = latestSolarData?.powerFeed
    )
  }

  private fun isRaining(latest: WeewxDataSet) = latest.rain > 0.0f

  fun fetchStatistics(): Statistics {
    val stats = Statistics()
    buildHistoricStatistics(stats)
    buildTodaysStatistics(stats)
    return stats
  }

  private fun buildHistoricStatistics(stats: Statistics) {
    val yesterday = dateUtil.yesterday()
    var dataSets: Collection<AggregatedWeatherDataSet> = pm.fetchAggregatedWeatherDataInRangeDesc(dateUtil.daysEarlier(yesterday, 29), yesterday)
    dataSets.forEach { wds -> wds.migrateRainCounter2DailyRain() }
    dataSets = removeDuplicates(dataSets)

    determineKindOfStatistics(stats, dataSets)

    var day = 1
    for (dataSet in dataSets) {
      val range = Statistics.TimeRange.byDay(day++)

      dataSet.dailyRain?.let {
        stats.addRain(range, it)
      }

      dataSet.solarRadiationTotal?.let {
        stats.addKwh(range, it / 1000) // Watt -> Kilowatt
      }

      dataSet.totalPowerProduction?.let { stats.addKwh(range, it / 1000) }

      dataSet.solarRadiationMax?.let {
        stats.updateSolarRadiation(range, it)
      }
      dataSet.powerProductionMax?.let {
        stats.updateSolarRadiation(range, it)
      }

      stats.setTemperature(range, dataSet.outsideTemperatureMax)
      stats.setTemperature(range, dataSet.outsideTemperatureMin)
    }
  }

  private fun determineKindOfStatistics(stats: Statistics, dataSets: Collection<AggregatedWeatherDataSet>) {
    if (!dataSets.isEmpty()) {
      if (dataSets.first().powerProductionMax != null) {
        stats.kind = Statistics.Kind.withSolarPower
      } else if (dataSets.first().solarRadiationMax != null) {
        stats.kind = Statistics.Kind.withSolarRadiation
      }
    }
  }

  private fun removeDuplicates(dataSets: Collection<AggregatedWeatherDataSet>): Collection<AggregatedWeatherDataSet> {
    val reducedList = LinkedHashMap<Date, AggregatedWeatherDataSet>()
    for (dataSet in dataSets) {
      reducedList.put(dataSet.date, dataSet)
    }
    return reducedList.values
  }

  private fun buildTodaysStatistics(stats: Statistics) {
    val latest: WeewxDataSet = pm.fetchYoungestDataSet()
    val latestSolarData: SolarDataSet? = pm.fetchCorrespondingSolarDataSet(latest.time)

    stats.rainLastHour = latest.rain

    stats.addRain(Statistics.TimeRange.today, latest.dailyRain)
    stats.setTemperature(Statistics.TimeRange.today, latest.temperature)
    updateSolarRadiation(latest.solarRadiation, stats)
    updateSolarRadiation(latestSolarData?.powerProduction, stats)

    val todaysDataSets = fetchTodaysDataSets()

    for (dataSet in todaysDataSets) {
      stats.setTemperature(Statistics.TimeRange.today, dataSet.outsideTemperature)
      updateSolarRadiation(dataSet.solarRadiationMax, stats)
      updateSolarRadiation(dataSet.powerProductionMax, stats)
      collectSolarRadiationTotal(dataSet.solarRadiation)
    }
    solarRadiationTotal?.let {
      stats.addKwh(Statistics.TimeRange.today, it)
    }
    val firstDataSetOfToday = todaysDataSets.firstOrNull()
    if (firstDataSetOfToday?.totalPowerProduction != null && latestSolarData != null) {
      stats.addKwh(Statistics.TimeRange.today, (latestSolarData.totalPowerProduction - firstDataSetOfToday.totalPowerProduction!!) / 1000)
    }
  }

  private fun updateSolarRadiation(solarRadiation: Float?, stats: Statistics) {
    solarRadiation?.let {
      stats.updateSolarRadiation(Statistics.TimeRange.today, it)
    }
  }

  private fun collectSolarRadiationTotal(solarRadiation: Float?) {
    solarRadiation?.let {
      // durch 4 teilen, weil es der Wert pro 1/4 Stunde ist
      // durch 1000 teilen, weil um von w/qm auf kw/qm zu konvertieren
      solarRadiationTotal = (solarRadiationTotal ?: 0.0f) + it / 4000
    }
  }

  fun fetchRainData(): RainDTO {
    val statistics = fetchStatistics()

    return statistics.toRainDTO()
  }

}

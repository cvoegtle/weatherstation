package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.data.RainDTO
import org.voegtle.weatherstation.server.data.Statistics
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.logic.caching.CachedWeatherDataProvider
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.*
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import java.util.logging.Logger

class WeatherDataFetcher(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {
    val log = Logger.getLogger(WeatherDataFetcher::class.java.simpleName)

    val weatherDataProvider = CachedWeatherDataProvider(pm, locationProperties.dateUtil)
    private val dateUtil: DateUtil = locationProperties.dateUtil

    fun getAggregatedWeatherData(begin: Date, end: Date): List<AggregatedWeatherDataSet> =
        pm.fetchAggregatedWeatherDataInRange(begin, end)

    fun fetchSmoothedWeatherData(begin: Date, end: Date?): MutableList<SmoothedWeatherDataSet> =
        if (end == null) {
            pm.fetchSmoothedWeatherDataInRange(begin)
        } else {
            pm.fetchSmoothedWeatherDataInRange(begin, end)
        }

    private fun fetchTodaysDataSets(): List<SmoothedWeatherDataSet> {
        val today = dateUtil.today()
        return pm.fetchSmoothedWeatherDataInRange(dateUtil.fromLocalToGMT(today))
    }

    fun getLatestWeatherDataUnformatted(authorized: Boolean): UnformattedWeatherDTO {
        val today = weatherDataProvider.getFirstSmoothedWeatherDataSetOfToday()
        val latest: WeatherDataSet = weatherDataProvider.getYoungestWeatherDataSet()
        val latestSolarData: SolarDataSet? = pm.fetchCorrespondingSolarDataSet(latest.timestamp)

        val twentyMinutesBefore = weatherDataProvider.getSmoothedWeatherDataSetMinutesBefore(20)
        val oneHourBefore = weatherDataProvider.getSmoothedWeatherDataSetMinutesBefore(60)

        return UnformattedWeatherDTO(time = latest.timestamp, localTime = dateUtil.toLocalTime(latest.timestamp),
            temperature = latest.outsideTemperature!!, humidity = latest.outsideHumidity,
            isRaining = isRaining(latest, twentyMinutesBefore),
            windspeed = if (locationProperties.isWindRelevant) latest.windspeed else null,
            insideTemperature = if (authorized) latest.insideTemperature else null,
            insideHumidity = if (authorized) latest.insideHumidity else null,
            rainLastHour = if (oneHourBefore != null)
                calculateRain(latest.rainCounter, oneHourBefore.rainCounter)
            else null,
            rainToday = if (today != null)
                calculateRain(latest.rainCounter, today.rainCounter)
            else null,
            powerFeed = latestSolarData?.powerFeed,
            powerProduction = latestSolarData?.powerProduction)
    }

    private fun isRaining(latest: WeatherDataSet, fifteenMinutesBefore: SmoothedWeatherDataSet?): Boolean {
        var raining = latest.isRaining ?: false

        if (latest.rainCounter != null && SmoothedWeatherDataSet.hasRainCounter(fifteenMinutesBefore)) {
            raining = raining || latest.rainCounter!! - fifteenMinutesBefore!!.rainCounter!! > 0
        }
        return raining
    }

    fun fetchStatistics(): Statistics {
        val stats = Statistics()
        buildHistoricStatistics(stats)
        buildTodaysStatistics(stats)
        return stats
    }

    private fun buildHistoricStatistics(stats: Statistics) {
        val yesterday = dateUtil.yesterday()
        log.warning("buildHistoricStatistics for $yesterday")
        var dataSets: Collection<AggregatedWeatherDataSet> = pm.fetchAggregatedWeatherDataInRange(
            dateUtil.daysEarlier(yesterday, 29), yesterday).reversed()
        dataSets = removeDuplicates(dataSets)
        var day = 1
        for (dataSet in dataSets) {
            val range = Statistics.TimeRange.byDay(day++)

            val rain = calculateRain(dataSet.rainCounter, 0)
            if (rain != null) {
                stats.addRain(range, rain)
            }

            dataSet.totalPowerProduction?.let {
                stats.addKwh(range, it / 1000)
            }
            dataSet.powerProductionMax?.let {
                stats.updateSolarRadiation(range, it)
            }

            stats.setTemperature(range, dataSet.outsideTemperatureMax)
            stats.setTemperature(range, dataSet.outsideTemperatureMin)
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
        val todaysDataSets = fetchTodaysDataSets()

        if (todaysDataSets.isNotEmpty()) {
            val firstSet = todaysDataSets[0]
            val latest: WeatherDataSet = weatherDataProvider.getYoungestWeatherDataSet()
            val latestSolarData: SolarDataSet? = pm.fetchCorrespondingSolarDataSet(latest.timestamp)

            val oneHourBefore = weatherDataProvider.getSmoothedWeatherDataSetMinutesBefore(60)
            stats.rainLastHour = calculateRain(latest, oneHourBefore)

            stats.addRain(Statistics.TimeRange.today, calculateRain(latest, firstSet))
            stats.setTemperature(Statistics.TimeRange.today, latest.outsideTemperature)

            for (dataSet in todaysDataSets) {
                stats.setTemperature(Statistics.TimeRange.today, dataSet.outsideTemperature)
                updateSolarRadiation(dataSet.powerProductionMax, stats)
            }

            if (firstSet.totalPowerProduction != null && latestSolarData != null) {
                stats.addKwh(Statistics.TimeRange.today, (latestSolarData.totalPowerProduction - firstSet.totalPowerProduction!!) / 1000)
            }
            updateSolarRadiation(latestSolarData?.powerProduction, stats)
        }
    }


    fun fetchRainData(): RainDTO {
        val statistics = fetchStatistics()

        return statistics.toRainDTO()
    }

    private fun calculateRain(latest: WeatherDataSet?, previous: SmoothedWeatherDataSet?): Float? {
        return if (latest == null || previous == null || latest.rainCounter == null || previous.rainCounter == null) {
            null
        } else calculateRain(latest.rainCounter!!, previous.rainCounter!!)
    }

    private fun calculateRain(youngerCount: Int?, olderCount: Int?): Float? {
        if (youngerCount == null || olderCount == null) {
            return null
        }
        val referenceCount = makeOverflowCorrection(olderCount, youngerCount)
        val rainCount = youngerCount - referenceCount
        return if (rainCount > 0) (0.295 * rainCount).toFloat() else null
    }

    private fun updateSolarRadiation(solarRadiation: Float?, stats: Statistics) {
        solarRadiation?.let {
            stats.updateSolarRadiation(Statistics.TimeRange.today, it)
        }
    }


}

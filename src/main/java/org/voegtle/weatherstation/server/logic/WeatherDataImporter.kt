package org.voegtle.weatherstation.server.logic

import org.voegtle.weatherstation.server.logic.caching.WeatherDataProvider
import org.voegtle.weatherstation.server.parser.DataLine
import org.voegtle.weatherstation.server.parser.DataParser
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import org.voegtle.weatherstation.server.request.ResponseCode
import java.text.ParseException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class WeatherDataImporter(private val pm: PersistenceManager, private val locationProperties: LocationProperties) {
    private val log = Logger.getLogger(WeatherDataImporter::class.java.name)
    val weatherDataProvider = WeatherDataProvider(pm)

    private val dateUtil = locationProperties.dateUtil
    private val importedUntil = findDateOfLastDataSet()
    private val dataIndicies = locationProperties.dataIndices
    var persisted: Int = 0
        private set

    private fun findDateOfLastDataSet(): Date {
        var lastImport = dateUtil.getDate(2023, 10, 1)

        val youngestDataSet = weatherDataProvider.getYoungestWeatherDataSet()
        if (youngestDataSet.timestamp.after(lastImport)) {
            lastImport = youngestDataSet.timestamp
        }

        val youngestSmoothedDS = pm.fetchYoungestSmoothedDataSet()
        if (youngestSmoothedDS != null && youngestSmoothedDS.timestamp.after(lastImport)) {
            lastImport = youngestSmoothedDS.timestamp
        }
        return lastImport
    }

    fun doImport(lines: ArrayList<DataLine>): String {
        var result: String
        try {
            log.info("Number of lines: " + lines.size)
            val parser = DataParser(dateUtil, dataIndicies)
            val dataSets = parser.parse(lines)
            log.info("Number of parsed datasets: " + dataSets.size)
            dataSets
                .filter { isNotOutdated(it) }
                .forEach {
                    weatherDataProvider.write(it)
                    persisted++
                }

            if (persisted > 0) {
                WeatherDataSmoother(pm, dateUtil).smoothWeatherData()
                WeatherDataAggregator(pm, dateUtil).aggregateWeatherData()
                WeatherDataForwarder(pm, locationProperties).forwardLastDataset()
            } else {
                log.warning("no dataset has been persisted")
            }

            result = if (persisted > 0) ResponseCode.ACKNOWLEDGE else ResponseCode.IGNORED
        } catch (ex: ParseException) {
            log.log(Level.SEVERE, "parsing failed", ex)
            result = ResponseCode.PARSE_ERROR
        }

        return result
    }

    private fun isNotOutdated(dataSet: WeatherDataSet): Boolean {
        val notOutdated = importedUntil.before(dataSet.timestamp)
        if (!notOutdated) {
            log.warning("WeatherDataSet from " + dataSet.timestamp + " is outdated. import until: " + importedUntil)
        }
        return notOutdated
    }

}

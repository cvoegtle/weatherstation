package org.voegtle.weatherstation.server.logic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet2
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import java.util.*

class WeatherDataFetcherTest {
  companion object {
    val locationProperties = createLocationProperties()
    val dateUtil = locationProperties.dateUtil
    val startTime: Date = dateUtil.today()
    val endTime: Date = dateUtil.incrementHour(startTime)
    val defaultResponse = ArrayList<SmoothedWeatherDataSet2>()
    val latestWeatherData = createDefaultWeatherDataSet()

    val twentyMinutesBefore: SmoothedWeatherDataSet2
    val oneHourBefore: SmoothedWeatherDataSet2

    init {
      val cal = Calendar.getInstance()
      cal.time = startTime
      for (minute in arrayOf(0, 15, 30, 45)) {
        cal.set(Calendar.MINUTE, minute)
        defaultResponse.add(
            createSmoothedWeatherDataSet(time = cal.time, temperature = 5.0f, humidity = 50.0f, rain = 1811))
      }

      twentyMinutesBefore = defaultResponse[2]

      cal.add(Calendar.MINUTE, -75)
      oneHourBefore = createSmoothedWeatherDataSet(time = cal.time, temperature = 4.0f, humidity = 60.0f, rain = 1810)
    }

    private fun createSmoothedWeatherDataSet(time: Date, temperature: Float, humidity: Float,
                                             rain: Int): SmoothedWeatherDataSet2 {
      var dataSet = SmoothedWeatherDataSet2(time)
      dataSet.rainCounter = rain
      dataSet.outsideTemperature = temperature
      dataSet.insideTemperature = temperature + 8
      dataSet.outsideHumidity = humidity
      dataSet.insideHumidity = humidity - 8
      return dataSet
    }

    private fun createDefaultWeatherDataSet(): WeatherDataSet {
      val wds = WeatherDataSet(endTime)
      wds.insideHumidity = 55.3f
      wds.outsideHumidity = 66.6f
      wds.insideTemperature = 22.8f
      wds.outsideTemperature = 4.5f
      wds.isRaining = false
      wds.rainCounter = 3222
      wds.windspeed = 10.7f
      wds.watt = 877.0f
      wds.kwh = 7.6
      return wds
    }


    private fun createLocationProperties(): LocationProperties {
      val lp = LocationProperties()
      lp.location = "development"
      lp.address = "Entwicklungs Station"
      lp.cityShortcut = "DEV"
      lp.city = "Tegelweg 8"
      lp.weatherForecast = ""
      lp.secretHash = "2fe3974d34634baf28c732f4793724f11e4a0813a84030f962187b3844485ae4"
      lp.readHash = "a883d58dbbb62d60da3893c9822d19e43bc371d20ccc5bfdb341f2b120eea54c"
      lp.indexInsideTemperature = 6
      lp.indexInsideHumidity = 14
      lp.expectedDataSets = 1000
      lp.expectedRequests = 1000
      lp.timezone = "Europe/Berlin"

      return lp
    }
  }

  @Test fun checkFetchSmoothed() {
    val mockedPersistenceManager = Mockito.mock(PersistenceManager::class.java)
    `when`(mockedPersistenceManager.fetchSmoothedWeatherDataInRange(startTime, endTime))
        .thenReturn(defaultResponse)

    val weatherDataFetcher = WeatherDataFetcher(mockedPersistenceManager, locationProperties)
    val result = weatherDataFetcher.fetchSmoothedWeatherData(startTime, endTime)
    Assertions.assertEquals(4, result.size)
    Assertions.assertEquals(5.0f, result[3].outsideTemperature)
  }

  @Test fun checkGetLatest() {
    val mockedPersistenceManager = Mockito.mock(PersistenceManager::class.java)
    `when`(mockedPersistenceManager.fetchYoungestDataSet()).thenReturn(latestWeatherData)
    `when`(mockedPersistenceManager.fetchDataSetMinutesBefore(endTime, 20)).thenReturn(twentyMinutesBefore)
    `when`(mockedPersistenceManager.fetchDataSetMinutesBefore(endTime, 60)).thenReturn(oneHourBefore)

    val weatherDataFetcher = WeatherDataFetcher(mockedPersistenceManager, locationProperties)
    val result = weatherDataFetcher.getLatestWeatherDataUnformatted(true)

    Assertions.assertEquals(22.8f, result.insideTemperature)

    val result2 = weatherDataFetcher.getLatestWeatherDataUnformatted(false)

    Assertions.assertEquals(null, result2.insideTemperature)
    Assertions.assertEquals(null, result2.windspeed)
  }

  @Test fun checkGetLatestWithWind() {
    val specialLocationProperties = createLocationProperties()
    specialLocationProperties.isWindRelevant = true

    val mockedPersistenceManager = Mockito.mock(PersistenceManager::class.java)
    `when`(mockedPersistenceManager.fetchYoungestDataSet()).thenReturn(latestWeatherData)
    `when`(mockedPersistenceManager.fetchDataSetMinutesBefore(endTime, 20)).thenReturn(twentyMinutesBefore)
    `when`(mockedPersistenceManager.fetchDataSetMinutesBefore(endTime, 60)).thenReturn(oneHourBefore)

    val weatherDataFetcher = WeatherDataFetcher(mockedPersistenceManager, specialLocationProperties)
    val result = weatherDataFetcher.getLatestWeatherDataUnformatted(true)

    Assertions.assertEquals(10.7f, result.windspeed)
  }


}

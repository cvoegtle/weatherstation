package org.voegtle.weatherstation.server.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import java.util.Date

class TestJSONConverter {
  val locationProperties = createLocationProperties()
  val dateUtil = locationProperties.dateUtil

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

  @Test fun checkToJsonUnformattedWeatherDTO() {

    val jsonConverter = JSONConverter(locationProperties)
    val time = Date()
    val dto = UnformattedWeatherDTO(time = time, localTime = dateUtil.toLocalTime(time), temperature = 5.1f,
                                    humidity = 51.0f, insideTemperature = 6.1f, insideHumidity = 61.0f,
                                    isRaining = true, rainLastHour = 0.3f, rainToday = 3.3f, windspeed = null,
                                    watt = 533.3f)
    val json = jsonConverter.toJson(dto)

    Assertions.assertFalse(json.has("wind"))
    Assertions.assertEquals(dateUtil.toLocalTime(time), json.getString("localtime"))
    Assertions.assertEquals(5, Math.round(json.getDouble("temperature")))
    Assertions.assertEquals(51, Math.round(json.getDouble("humidity")))
  }

}
package org.voegtle.weatherstation.server.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO
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

  @Test fun checkToJsonCacheWeatherDTO() {
    val jsonConverter = JSONConverter(locationProperties)
    val time = dateUtil.today()
    val dto = CacheWeatherDTO(id = "hallo", time = time,
                              localTime = dateUtil.toLocalTime(time),
                              location = "testcity",
                              locationShort = "TC",
                              latitude = 53.2f,
                              longitude = 8.3f,
                              humidity = 51.3f,
                              temperature = -4.6f,
                              insideTemperature = 20.4f,
                              insideHumidity = 65.0f,
                              rainLastHour = 0.295f,
                              rainToday = 2.555f,
                              isRaining = true,
                              watt = 712.9f,
                              windspeed = 33.5f)

    val json = jsonConverter.toJson(dto)

    Assertions.assertTrue(json.has("wind"))
    Assertions.assertTrue(json.has("watt"))
    Assertions.assertFalse(json.has("windspeed"))
    Assertions.assertEquals(-4.6f, json.get("temperature"))
    Assertions.assertEquals(712.9f, json.get("watt"))
  }

  @Test fun checkDecodeCacheWeatherDTO() {
    val input = "{\"id\":\"leoxity\",\"timestamp\":\"Sun Dec 03 12:20:25 UTC 2017\",\"localtime\":\"13:20\",\"wind\":0,\"location_short\":\"Leo\",\"humidity\":95,\"raining\":false,\"forecast\":\"http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage\",\"location\":\"Leopoldshöhe\",\"watt\":15,\"longitude\":8.695692,\"latitude\":52.014572,\"temperature\":0.6}"

    val jsonConverter = JSONConverter(locationProperties)
    val dto = jsonConverter.decodeWeatherDTO(input)
    Assertions.assertEquals("leoxity", dto.id)
    Assertions.assertEquals("13:20", dto.localTime)
    Assertions.assertEquals(8.695692f, dto.longitude)
    Assertions.assertEquals(52.014572f, dto.latitude)
    Assertions.assertEquals(0.6f, dto.temperature)
    Assertions.assertEquals(15.0f, dto.watt)
    Assertions.assertNull(dto.insideHumidity)
    Assertions.assertNull(dto.insideTemperature)
  }

  @Test fun checkDecodeCacheWeatherDTOInside() {
    val input = "{\"id\":\"tegelweg8\",\"inside_humidity\":49,\"timestamp\":\"Sun Dec 03 12:37:43 UTC 2017\",\"localtime\":\"13:37\",\"location_short\":\"PB\",\"humidity\":91,\"raining\":false,\"forecast\":\"http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage\",\"location\":\"Paderborn\",\"inside_temperature\":19.1,\"longitude\":8.758523,\"latitude\":51.723778,\"rain_today\":0.295,\"temperature\":0.8}"

    val jsonConverter = JSONConverter(locationProperties)
    val dto = jsonConverter.decodeWeatherDTO(input)
    Assertions.assertEquals("tegelweg8", dto.id)
    Assertions.assertEquals("13:37", dto.localTime)
    Assertions.assertEquals(8.758523f, dto.longitude)
    Assertions.assertEquals(51.723778f, dto.latitude)
    Assertions.assertEquals(0.8f, dto.temperature)
    Assertions.assertNull(dto.watt)
    Assertions.assertNull(dto.windspeed)
    Assertions.assertEquals(49.0f, dto.insideHumidity)
    Assertions.assertEquals(19.1f, dto.insideTemperature)
  }

}
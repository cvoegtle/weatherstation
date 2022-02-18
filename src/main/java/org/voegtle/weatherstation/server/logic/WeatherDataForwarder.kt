package org.voegtle.weatherstation.server.logic

import org.springframework.web.client.RestTemplate
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO
import org.voegtle.weatherstation.server.persistence.CacheWeatherDTO
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import java.io.IOException
import java.util.logging.Logger

internal class WeatherDataForwarder(pm: PersistenceManager, locationProperties: LocationProperties) {
    private val log = Logger.getLogger(WeatherDataForwarder::class.simpleName)

    private val weatherDataFetcher = WeatherDataFetcher(pm, locationProperties)
    private val locationProperties = locationProperties

    fun forwardLastDataset() {
        val latest = weatherDataFetcher.getLatestWeatherDataUnformatted(true)
        val cacheWeatherDTO = toCachedWeatherDTO(latest)
        forward(cacheWeatherDTO)
    }

    private fun forward(cacheWeatherDTO: CacheWeatherDTO) {
        try {
            val restTemplate = RestTemplate()
            log.info("forwarding $cacheWeatherDTO")
            restTemplate.postForObject("https://wettercentral.appspot.com/weatherstation/cache2", cacheWeatherDTO, String::class.java)
        } catch (e: IOException) {
            log.warning("failed forwarding to wettercentral. " + e.message)
        }

    }

    private fun toCachedWeatherDTO(latest: UnformattedWeatherDTO) = CacheWeatherDTO(
        id = locationProperties.location,
        time = latest.time,
        temperature = latest.temperature,
        insideTemperature = latest.insideTemperature,
        humidity = latest.humidity,
        insideHumidity = latest.insideHumidity,
        rainLastHour = if (latest.rainLastHour > 0.0f) latest.rainLastHour else null,
        rainToday = if (latest.rainToday > 0.0f) latest.rainToday else null,
        raining = latest.isRaining,
        windspeed = toKilometerPerHour(latest.windspeed),
        barometer = latest.barometer,
        solarradiation = latest.solarradiation,
        UV = latest.UV,
        powerProduction = latest.powerProduction,
        powerFeed = latest.powerFeed,
        location = latest.location,
        locationShort = locationProperties.cityShortcut,
        localTime = latest.localtime,

        forecast = locationProperties.weatherForecast,

        latitude = locationProperties.latitude,
        longitude = locationProperties.longitude
    )
}

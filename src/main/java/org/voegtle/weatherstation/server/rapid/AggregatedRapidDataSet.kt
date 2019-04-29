package org.voegtle.weatherstation.server.rapid


class AggregatedRapidDataSet {
  val period: Period = Period()
  val temperature = AggregatedFloat()
  val humidity = AggregatedFloat()
  val barometer = AggregatedFloat()
  var dailyRain = 0.0f
  var rain = 0.0f
  val UV = AggregatedFloat()
  val solarRadiation = AggregatedFloat()
  var windDirection: Int? = null
  val windSpeed = AggregatedFloat()
  val windGust = MaxFloat()
  val indoorTemperature = AggregatedFloat()
  val indoorHumidity = AggregatedFloat()


  constructor(dataset: RapidDataSet) {
    period.start = dataset.time
    add(dataset)
  }

  fun add(dataset: RapidDataSet) {
    period.end = dataset.time
    temperature.add(dataset.temperature)
    humidity.add(dataset.humidity)
    barometer.add(dataset.barometer)
    dailyRain = dataset.dailyRain
    rain = dataset.rain
    UV.add(dataset.UV)
    solarRadiation.add(dataset.solarRadiation)
    windDirection = dataset.windDirection
    windSpeed.add(dataset.windSpeed)
    windGust.value = dataset.windGust
    indoorTemperature.add(dataset.indoorTemperature)
    indoorHumidity.add(dataset.indoorHumidity)
  }

  fun age() = period.length()
  fun time() = period.end



}
package org.voegtle.weatherstation.server.parser

import org.voegtle.weatherstation.server.persistence.DataIndicies
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import org.voegtle.weatherstation.server.util.StringUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DataParser(dateUtil: DateUtil, di: DataIndicies) {

  private val MIN_DATE = dateUtil.getDate(2017, 11, 1)

  private val indexOutsideTemperature = di.indexOutsideTemperature ?: INDEX_OUTSIDE_TEMPERATURE
  private val indexOutsideHumidity = di.indexOutsideHumidity ?: INDEX_OUTSIDE_HUMIDITY
  private val indexInsideTemperature = di.indexInsideTemperature ?: INDEX_INSIDE_TEMPERATURE
  private val indexInsideHumidity = di.indexInsideHumidity ?: INDEX_INSIDE_HUMIDITY

  @Throws(ParseException::class)
  fun parse(lines: List<DataLine>): List<WeatherDataSet> {
    val dataSets = ArrayList<WeatherDataSet>()

    var lastDataSet: WeatherDataSet? = null
    for (line in lines) {
      val dataSet = parse(line)
      if (dataSet != null) {
        lastDataSet = dataSet
        dataSets.add(lastDataSet)
      }
    }

    repairDate(lastDataSet)

    return dataSets
  }

  /**
   * es kann vorkommen, dass das Datum der Fritzbox falsch gesetzt ist. Meist 1970.
   * In diesem Fall zumindest für den letzten Datum die aktuelle Zeit setzen.
   * Denn dieser Datensatz hat die Übertragung ausgelöst.
   */
  private fun repairDate(dataSet: WeatherDataSet?) {

    if (dataSet != null && dataSet.timestamp.before(MIN_DATE)) {
      dataSet.timestamp = Date()
    }
  }

  @Throws(ParseException::class)
  private fun parse(data: DataLine): WeatherDataSet? {
    if (isValid(data)) {
      val timestamp = getTimestamp(data)

      val dataSet = WeatherDataSet(timestamp)
      dataSet.insideTemperature = parseFloat(data[indexInsideTemperature])
      dataSet.insideHumidity = parseFloat(data[indexInsideHumidity])

      dataSet.outsideTemperature = parseFloat(data[indexOutsideTemperature])
      dataSet.outsideHumidity = parseFloat(data[indexOutsideHumidity]) ?: 0.0f

      dataSet.rainCounter = parseInteger(data[INDEX_RAINCOUNTER])
      dataSet.isRaining = parseBoolean(data[INDEX_RAINING])

      dataSet.windspeed = parseFloat(data[INDEX_WIND_SPEED])
      dataSet.watt = parseFloat(data[INDEX_WATT])
      dataSet.kwh = parseDouble(data[INDEX_KWH])

      return dataSet
    } else {
      return null
    }
  }

  @Throws(ParseException::class)
  private fun getTimestamp(data: DataLine): Date {
    val timeString = data[INDEX_DATE]
    if (StringUtil.isEmpty(timeString)) {
      val cal = Calendar.getInstance(Locale.GERMANY)
      cal.set(Calendar.MILLISECOND, 0)
      return cal.time
    }
    return parseTimestamp(timeString!!)
  }

  private fun isValid(data: DataLine): Boolean {
    return data.size() > INDEX_DATE && StringUtil.isNotEmpty(data[indexOutsideTemperature])
  }

  @Throws(ParseException::class)
  private fun parseTimestamp(value: String): Date {
    return sdf.parse(value)
  }

  private fun parseFloat(value: String?): Float? {
    return if (StringUtil.isEmpty(value)) {
      null
    } else java.lang.Float.parseFloat(value!!.replace(',', '.'))
  }

  private fun parseDouble(value: String?): Double? {
    return if (StringUtil.isEmpty(value)) {
      null
    } else java.lang.Double.parseDouble(value!!.replace(',', '.'))
  }

  private fun parseInteger(value: String?): Int? {
    return if (StringUtil.isEmpty(value)) {
      null
    } else Integer.parseInt(value!!)
  }

  private fun parseBoolean(value: String?): Boolean? {
    return if (StringUtil.isEmpty(value)) {
      null
    } else "1" == value
  }

  companion object {
    // 1;1;;;;;;;;;;;;;;;;;;15,1;78;0,0;616;0;0

    private val INDEX_INSIDE_TEMPERATURE = 7
    private val INDEX_INSIDE_HUMIDITY = 15
    private val INDEX_OUTSIDE_TEMPERATURE = 19
    private val INDEX_OUTSIDE_HUMIDITY = 20
    private val INDEX_WIND_SPEED = 21
    private val INDEX_RAINCOUNTER = 22
    private val INDEX_RAINING = 23
    private val INDEX_DATE = 25
    private val INDEX_WATT = 26
    private val INDEX_KWH = 27

    private val FORMAT_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ssZ"
    private val sdf = SimpleDateFormat(FORMAT_TIMESTAMP)
  }
}

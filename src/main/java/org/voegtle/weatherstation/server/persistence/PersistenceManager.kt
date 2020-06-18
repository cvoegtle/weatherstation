package org.voegtle.weatherstation.server.persistence

import com.googlecode.objectify.ObjectifyService
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.Contact
import org.voegtle.weatherstation.server.persistence.entities.Health
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.util.DateUtil
import org.voegtle.weatherstation.server.weewx.WeewxDataSet
import java.util.Date
import java.util.logging.Logger

open class PersistenceManager {
  companion object {
    private val log = Logger.getLogger(PersistenceManager::class.java.name)
  }

  fun makePersistant(dataSet: WeewxDataSet) {
    ObjectifyService.ofy().save().entity(dataSet).now()
  }

  fun makePersistant(dataSet: SmoothedWeatherDataSet) {
    ObjectifyService.ofy().save().entity(dataSet).now()
  }

  fun makePersistant(lp: LocationProperties) {
    ObjectifyService.ofy().save().entity(lp).now()
  }

  fun updateDataset(ds: SmoothedWeatherDataSet) {
    val managedDS = ObjectifyService.ofy().load().type(SmoothedWeatherDataSet::class.java).id(ds.id!!).now()

    managedDS.outsideHumidity = ds.outsideHumidity
    managedDS.outsideTemperature = ds.outsideTemperature
    managedDS.dailyRain = ds.dailyRain
    managedDS.insideTemperature = ds.insideTemperature
    managedDS.insideHumidity = ds.insideHumidity
    managedDS.kwh = ds.kwh
    managedDS.repaired = ds.repaired

    makePersistant(managedDS)
  }

  fun removeDataset(ds: SmoothedWeatherDataSet) {
    ObjectifyService.ofy().delete().type(SmoothedWeatherDataSet::class.java).id(ds.id!!).now()
  }

  fun makePersistant(dataSet: AggregatedWeatherDataSet) {
    ObjectifyService.ofy().save().entity(dataSet).now()
  }

  fun fetchDataSetMinutesBefore(referenceDate: Date, minutes: Int): SmoothedWeatherDataSet? {
    val minAge = DateUtil.minutesBefore(referenceDate, minutes)
    val maxAge = DateUtil.minutesBefore(referenceDate, minutes + 30)

    val result = ObjectifyService.ofy()
        .load()
        .type(SmoothedWeatherDataSet::class.java)
        .filter("timestamp <", minAge)
        .filter("timestamp >", maxAge)
        .order("-timestamp")
        .first()
        .safe()

    log.info("found dataset: " + result.timestamp)
    return result
  }

  fun fetchYoungestDataSet(): WeewxDataSet = ObjectifyService.ofy()
      .load()
      .type(WeewxDataSet::class.java)
      .order("-time")
      .first()
      .safe()

  fun fetchWeatherDataInRange(begin: Date, end: Date): List<WeewxDataSet> = ObjectifyService.ofy().load()
      .type(WeewxDataSet::class.java)
      .filter("time >=", begin)
      .filter("time <=", end)
      .order("time")
      .list()


  fun fetchSmoothedWeatherDataInRange(begin: Date, end: Date): MutableList<SmoothedWeatherDataSet> =
      ObjectifyService.ofy().load()
          .type(SmoothedWeatherDataSet::class.java)
          .filter("timestamp >=", begin)
          .filter("timestamp <=", end)
          .order("timestamp")
          .list()

    fun fetchSmoothedWeatherDataInRange(begin: Date): MutableList<SmoothedWeatherDataSet> =
      ObjectifyService.ofy().load()
          .type(SmoothedWeatherDataSet::class.java)
          .filter("timestamp >=", begin)
          .order("timestamp")
          .list()

  fun fetchAggregatedWeatherDataInRange(begin: Date, end: Date): List<AggregatedWeatherDataSet> = ObjectifyService.ofy().load()
      .type(AggregatedWeatherDataSet::class.java)
      .filter("date >=", begin)
      .filter("date <=", end)
      .order("date")
      .list()

  fun fetchAggregatedWeatherDataInRange(begin: Date): List<AggregatedWeatherDataSet> = ObjectifyService.ofy().load()
      .type(AggregatedWeatherDataSet::class.java)
      .filter("date >=", begin)
      .order("date")
      .list()

  fun fetchAggregatedWeatherDataInRangeDesc(begin: Date, end: Date): List<AggregatedWeatherDataSet> = ObjectifyService.ofy().load()
      .type(AggregatedWeatherDataSet::class.java)
      .filter("date >=", begin)
      .filter("date <=", end)
      .order("-date")
      .list()

  fun fetchOldestSmoothedDataSetInRange(begin: Date, end: Date): SmoothedWeatherDataSet? = ObjectifyService.ofy().load()
      .type(SmoothedWeatherDataSet::class.java)
      .filter("timestamp >=", begin)
      .filter("timestamp <=", end)
      .order("timestamp")
      .limit(1)
      .list()
      .firstOrNull()

  fun fetchYoungestSmoothedDataSet(): SmoothedWeatherDataSet? = ObjectifyService.ofy().load()
      .type(SmoothedWeatherDataSet::class.java)
      .order("-timestamp")
      .firstOrNull()

  fun fetchYoungestAggregatedDataSet(): AggregatedWeatherDataSet? = ObjectifyService.ofy().load()
      .type(AggregatedWeatherDataSet::class.java)
      .order("-date")
      .limit(1)
      .list()
      .firstOrNull()

  fun removeWeatherDataInRange(begin: Date, end: Date) {
    val ids = ObjectifyService.ofy().load()
        .type(WeewxDataSet::class.java)
        .filter("time >=", begin)
        .filter("time <=", end)
        .list()
        .map { it.id }

    ObjectifyService.ofy().delete().type(WeewxDataSet::class.java).ids(ids).now()
  }

  fun fetchLocationProperties(): LocationProperties = ObjectifyService.ofy().load()
      .type(LocationProperties::class.java)
      .limit(1)
      .first().now()

  fun selectHealth(day: Date): Health {
    val health = ObjectifyService.ofy().load().type(Health::class.java)
        .filter("day", day)
        .limit(1)
        .list()
        .firstOrNull()
    return health ?: Health(day)
  }

  fun makePersistant(dto: HealthDTO) {
    val health = selectHealth(dto.day)
    health.fromDTO(dto)
    ObjectifyService.ofy().save().entity(health)
  }

  fun makePersistant(contact: Contact) {
    ObjectifyService.ofy().save().entity(contact)
  }

  fun fetchContacts(): List<Contact> = ObjectifyService.ofy().load().type(Contact::class.java).list()

}

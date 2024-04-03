package org.voegtle.weatherstation.server.persistence

import com.googlecode.objectify.ObjectifyService
import org.voegtle.weatherstation.server.persistence.entities.*
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import java.util.logging.Logger

open class PersistenceManager {
    companion object {
        private val log = Logger.getLogger(PersistenceManager::class.java.name)
    }

    fun makePersistent(dataSet: WeatherDataSet) {
        ObjectifyService.ofy().save().entity(dataSet).now()
    }

    fun makePersistent(dataSet: SmoothedWeatherDataSet2) {
        ObjectifyService.ofy().save().entity(dataSet).now()
    }

    fun makePersistent(id: ImageIdentifier) {
        ObjectifyService.ofy().save().entity(id).now()
    }

    fun makePersistent(lp: LocationProperties) {
        ObjectifyService.ofy().save().entity(lp).now()
    }

    fun updateDataset(ds: SmoothedWeatherDataSet2) {
        val managedDS = ObjectifyService.ofy().load().type(SmoothedWeatherDataSet2::class.java).id(ds.id!!).now()

        managedDS.outsideHumidity = ds.outsideHumidity
        managedDS.outsideTemperature = ds.outsideTemperature
        managedDS.rainCounter = ds.rainCounter
        managedDS.isRaining = ds.isRaining
        managedDS.insideTemperature = ds.insideTemperature
        managedDS.insideHumidity = ds.insideHumidity
        managedDS.totalPowerProduction = ds.totalPowerProduction
        managedDS.repaired = ds.repaired

        makePersistent(managedDS)
    }

    fun removeDataset(ds: SmoothedWeatherDataSet2) {
        ObjectifyService.ofy().delete().type(SmoothedWeatherDataSet2::class.java).id(ds.id!!).now()
        log.warning("removeDataset() ds.timestamp: " + ds.timestamp)
    }

    fun makePersistent(dataSet: AggregatedWeatherDataSet) {
        ObjectifyService.ofy().save().entity(dataSet).now()
    }

    fun fetchDataSetMinutesBefore(referenceDate: Date, minutes: Int): SmoothedWeatherDataSet2 {
        val minAge = DateUtil.minutesBefore(referenceDate, minutes)
        val maxAge = DateUtil.minutesBefore(referenceDate, minutes + 30)

        val result = ObjectifyService.ofy()
            .load()
            .type(SmoothedWeatherDataSet2::class.java)
            .filter("timestamp <", minAge)
            .filter("timestamp >", maxAge)
            .order("-timestamp")
            .limit(1)
            .first()
            .safe()

        log.info("fetchDataSetMinutesBefore() found dataset: " + result.timestamp)
        return result
    }

    fun fetchYoungestDataSet(): WeatherDataSet {
        log.info("fetchYoungestDataSet()")

        return ObjectifyService.ofy()
            .load()
            .type(WeatherDataSet::class.java)
            .order("-timestamp")
            .limit(1)
            .first()
            .safe()
    }

    fun fetchWeatherDataInRange(begin: Date, end: Date): List<WeatherDataSet> {
        val list = ObjectifyService.ofy().load()
            .type(WeatherDataSet::class.java)
            .filter("timestamp >=", begin)
            .filter("timestamp <=", end)
            .order("timestamp")
            .list()
        log.info("fetchWeatherDataInRange() list.size=" + list.size)
        return list
    }

    fun fetchSmoothedWeatherDataInRange(begin: Date, end: Date): MutableList<SmoothedWeatherDataSet2> {
        val list = ObjectifyService.ofy().load()
            .type(SmoothedWeatherDataSet2::class.java)
            .filter("timestamp >=", begin)
            .filter("timestamp <=", end)
            .order("timestamp")
            .list()
        log.info("fetchSmoothedWeatherDataInRange() list.size=" + list.size)
        return list;
    }

    fun fetchSmoothedWeatherDataInRange(begin: Date): MutableList<SmoothedWeatherDataSet2> {
        val list = ObjectifyService.ofy().load()
            .type(SmoothedWeatherDataSet2::class.java)
            .filter("timestamp >=", begin)
            .order("timestamp")
            .list()
        log.info("fetchSmoothedWeatherDataInRange() list.size=" + list.size)
        return list
    }

    fun fetchAggregatedWeatherDataInRange(begin: Date, end: Date): List<AggregatedWeatherDataSet> {
        val list = ObjectifyService.ofy().load()
            .type(AggregatedWeatherDataSet::class.java)
            .filter("date >=", begin)
            .filter("date <=", end)
            .order("date")
            .list()
        log.info("fetchAggregatedWeatherDataInRange() list.size=" + list.size)
        return list
    }

    fun fetchOldestSmoothedDataSetInRange(begin: Date, end: Date): SmoothedWeatherDataSet2? {
        val dataSet = ObjectifyService.ofy().load()
            .type(SmoothedWeatherDataSet2::class.java)
            .filter("timestamp >=", begin)
            .filter("timestamp <=", end)
            .order("timestamp")
            .limit(1)
            .firstOrNull()
        log.info("fetchOldestSmoothedDataSetInRange() = "  + (dataSet?.timestamp ?: "null"))
        return dataSet
    }

    fun fetchYoungestSmoothedDataSet(): SmoothedWeatherDataSet2? {
        val youngest = ObjectifyService.ofy().load()
            .type(SmoothedWeatherDataSet2::class.java)
            .order("-timestamp")
            .limit(1)
            .firstOrNull()
        log.info("fetchYoungestSmoothedDataSet() = "  + (youngest?.timestamp ?: "null"))
        return youngest
    }

    fun fetchYoungestAggregatedDataSet(): AggregatedWeatherDataSet? {
        val youngest = ObjectifyService.ofy().load()
            .type(AggregatedWeatherDataSet::class.java)
            .order("-date")
            .limit(1)
            .firstOrNull()
        log.info("fetchYoungestAggregatedDataSet() = "  + (youngest?.date ?: "null"))
        return youngest
    }

    fun removeWeatherDataInRange(begin: Date, end: Date) {
        val ids = ObjectifyService.ofy().load()
            .type(WeatherDataSet::class.java)
            .filter("timestamp >=", begin)
            .filter("timestamp <=", end)
            .list()
            .map { it.id }
        log.info("removeWeatherDataInRange() ids.size= " + ids.size)
        ObjectifyService.ofy().delete().type(WeatherDataSet::class.java).ids(ids).now()
    }

    fun fetchImageIdentifiers(): List<ImageIdentifier> {
        val list = ObjectifyService.ofy().load()
            .type(ImageIdentifier::class.java).list()
        log.info("fetchImageIdentifiers() list.size= " + list.size)
        return list
    }


    fun fetchLocationProperties(): LocationProperties {
        val locationProperties = ObjectifyService.ofy().load()
            .type(LocationProperties::class.java)
            .limit(1)
            .first().now()
        log.info("fetchLocationProperties()")
        return locationProperties
    }


    fun selectHealth(day: Date): Health {
        val health = ObjectifyService.ofy().load().type(Health::class.java)
            .filter("day", day)
            .limit(1)
            .list()
            .firstOrNull()
        log.info("selectHealth() = "  + (health?.day ?: "null"))

        return health ?: Health(day)
    }

    fun makePersistent(dto: HealthDTO) {
        val health = selectHealth(dto.day)
        health.fromDTO(dto)
        ObjectifyService.ofy().save().entity(health)
    }

    fun makePersistent(contact: Contact) {
        ObjectifyService.ofy().save().entity(contact)
    }

    fun fetchContacts(): List<Contact> = ObjectifyService.ofy().load().type(Contact::class.java).list()

    fun makePersistent(dataSet: SolarDataSet) {
        ObjectifyService.ofy().save().entity(dataSet).now()
    }

    fun fetchCorrespondingSolarDataSet(time: Date): SolarDataSet? = ObjectifyService.ofy()
        .load()
        .type(SolarDataSet::class.java)
        .filter("time <=", time)
        .filter("time >=", DateUtil.minutesBefore(time, 15))
        .order("-time")
        .first()
        .now()

    fun fetchSolarDataInRange(begin: Date, end: Date): List<SolarDataSet> = ObjectifyService.ofy().load()
        .type(SolarDataSet::class.java)
        .filter("time >=", begin)
        .filter("time <=", end)
        .order("time")
        .list()

    fun removeSolarDataInRange(begin: Date, end: Date) {
        val ids = ObjectifyService.ofy().load()
            .type(SolarDataSet::class.java)
            .filter("time >=", begin)
            .filter("time <=", end)
            .list()
            .map { it.id }

        ObjectifyService.ofy().delete().type(SolarDataSet::class.java).ids(ids).now()
    }



}

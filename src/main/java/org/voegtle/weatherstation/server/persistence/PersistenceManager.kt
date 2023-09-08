package org.voegtle.weatherstation.server.persistence

import com.googlecode.objectify.ObjectifyService
import org.voegtle.weatherstation.server.image.Image
import org.voegtle.weatherstation.server.persistence.entities.*
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.*
import java.util.logging.Logger

open class PersistenceManager {
    companion object {

        private val log = Logger.getLogger(PersistenceManager::class.java.name)
    }

    fun makePersitant(dataSet: WeatherDataSet) {
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

    fun makePersistent(location: WeatherLocation) {
        ObjectifyService.ofy().save().entity(location).now()
    }

    fun makePersistent(image: Image) {
        ObjectifyService.ofy().save().entity(image).now()
    }

    fun clearImages() {
        val keys = ObjectifyService.ofy().load().type(Image::class.java).keys().list()
        ObjectifyService.ofy().delete().keys(keys).now()
    }

    fun countImages(): Int {
        val keys = ObjectifyService.ofy().load().type(Image::class.java).keys().list()
        return keys.size
    }


    fun fetchImage(oid: String): Image? = ObjectifyService.ofy()
            .load()
            .type(Image::class.java)
            .id(oid)
            .now()

    fun fetchWeatherLocations(): HashMap<String, WeatherLocation> {
        val weatherLocations = ObjectifyService.ofy().load()
            .type(WeatherLocation::class.java)
            .list()
        val map = HashMap<String, WeatherLocation>()
        weatherLocations.forEach { map[it.location] = it }
        return map
    }

    fun updateDataset(ds: SmoothedWeatherDataSet2) {
        val managedDS = ObjectifyService.ofy().load().type(SmoothedWeatherDataSet2::class.java).id(ds.id!!).now()

        managedDS.outsideHumidity = ds.outsideHumidity
        managedDS.outsideTemperature = ds.outsideTemperature
        managedDS.rainCounter = ds.rainCounter
        managedDS.isRaining = ds.isRaining
        managedDS.insideTemperature = ds.insideTemperature
        managedDS.insideHumidity = ds.insideHumidity
        managedDS.kwh = ds.kwh
        managedDS.repaired = ds.repaired

        makePersistent(managedDS)
    }

    fun removeDataset(ds: SmoothedWeatherDataSet2) {
        ObjectifyService.ofy().delete().type(SmoothedWeatherDataSet2::class.java).id(ds.id!!).now()
    }

    fun makePersitant(dataSet: AggregatedWeatherDataSet) {
        ObjectifyService.ofy().save().entity(dataSet).now()
    }

    fun fetchDataSetMinutesBefore(referenceDate: Date, minutes: Int): SmoothedWeatherDataSet2? {
        val minAge = DateUtil.minutesBefore(referenceDate, minutes)
        val maxAge = DateUtil.minutesBefore(referenceDate, minutes + 30)

        val result = ObjectifyService.ofy()
            .load()
            .type(SmoothedWeatherDataSet2::class.java)
            .filter("timestamp <", minAge)
            .filter("timestamp >", maxAge)
            .order("-timestamp")
            .first()
            .safe()

        log.info("found dataset: " + result.timestamp)
        return result
    }

    fun fetchYoungestDataSet(): WeatherDataSet = ObjectifyService.ofy()
        .load()
        .type(WeatherDataSet::class.java)
        .order("-timestamp")
        .first()
        .safe()

    fun fetchWeatherDataInRange(begin: Date, end: Date): List<WeatherDataSet> = ObjectifyService.ofy().load()
        .type(WeatherDataSet::class.java)
        .filter("timestamp >=", begin)
        .filter("timestamp <=", end)
        .order("timestamp")
        .list()

    fun fetchSmoothedWeatherDataInRange(begin: Date, end: Date): MutableList<SmoothedWeatherDataSet2> =
        ObjectifyService.ofy().load()
            .type(SmoothedWeatherDataSet2::class.java)
            .filter("timestamp >=", begin)
            .filter("timestamp <=", end)
            .order("timestamp")
            .list()

    fun fetchSmoothedWeatherDataInRange(begin: Date): MutableList<SmoothedWeatherDataSet2> =
        ObjectifyService.ofy().load()
            .type(SmoothedWeatherDataSet2::class.java)
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

    fun fetchOldestSmoothedDataSetInRange(begin: Date, end: Date): SmoothedWeatherDataSet2? = ObjectifyService.ofy().load()
        .type(SmoothedWeatherDataSet2::class.java)
        .filter("timestamp >=", begin)
        .filter("timestamp <=", end)
        .order("timestamp")
        .limit(1)
        .list()
        .firstOrNull()

    fun fetchYoungestSmoothedDataSet(): SmoothedWeatherDataSet2? = ObjectifyService.ofy().load()
        .type(SmoothedWeatherDataSet2::class.java)
        .order("-timestamp")
        .limit(1)
        .list()
        .firstOrNull()

    fun fetchYoungestAggregatedDataSet(): AggregatedWeatherDataSet? = ObjectifyService.ofy().load()
        .type(AggregatedWeatherDataSet::class.java)
        .order("-date")
        .limit(1)
        .list()
        .firstOrNull()

    fun removeWeatherDataInRange(begin: Date, end: Date) {
        val ids = ObjectifyService.ofy().load()
            .type(WeatherDataSet::class.java)
            .filter("timestamp >=", begin)
            .filter("timestamp <=", end)
            .list()
            .map { it.id }

        ObjectifyService.ofy().delete().type(WeatherDataSet::class.java).ids(ids).now()
    }

    fun fetchImageIdentifiers(): List<ImageIdentifier> =
        ObjectifyService.ofy().load()
            .type(ImageIdentifier::class.java).list()


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

    fun makePersistent(dto: HealthDTO) {
        val health = selectHealth(dto.day)
        health.fromDTO(dto)
        ObjectifyService.ofy().save().entity(health)
    }

    fun makePersistent(contact: Contact) {
        ObjectifyService.ofy().save().entity(contact)
    }

    fun fetchContacts(): List<Contact> = ObjectifyService.ofy().load().type(Contact::class.java).list()

}

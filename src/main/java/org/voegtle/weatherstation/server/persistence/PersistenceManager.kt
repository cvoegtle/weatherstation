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

  fun makePersitant(dataSet: SmoothedWeatherDataSet) {
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


  fun fetchImage(oid: String): Image? {

    val result = ObjectifyService.ofy()
      .load()
      .type(Image::class.java)
      .filter("oid =", oid)
      .first()
      .safe()

    return result
  }

  fun fetchWeatherLocations(): HashMap<String, WeatherLocation> {
    val result = HashMap<String, WeatherLocation>()
    val em = factory.createEntityManager()

    try {
      val q = em.createQuery("SELECT location FROM WeatherLocation location")
      (q.resultList as List<WeatherLocation>).forEach {result.put(it.location, it)}
    } finally {
      em.close()
    }
    return result
  }

  fun updateDataset(ds: SmoothedWeatherDataSet) {
    val em = factory.createEntityManager()

    try {
      em.transaction.begin()
      val managedDS = em.find(SmoothedWeatherDataSet::class.java, ds.key)
      managedDS.outsideHumidity = ds.outsideHumidity
      managedDS.outsideTemperature = ds.outsideTemperature
      managedDS.rainCounter = ds.rainCounter
      managedDS.isRaining = ds.isRaining
      managedDS.insideTemperature = ds.insideTemperature
      managedDS.insideHumidity = ds.insideHumidity
      managedDS.kwh = ds.kwh
      managedDS.repaired = ds.repaired
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun updateSmoothedDataset(ds: SmoothedWeatherDataSet) {
    val em = factory.createEntityManager()

    try {
      em.transaction.begin()
      val managedDS = em.find(SmoothedWeatherDataSet::class.java, ds.key)
      managedDS.dailyRain = ds.dailyRain
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun upgradeAggregatedDataset(ds: AggregatedWeatherDataSet) {
    val em = factory.createEntityManager()

    try {
      em.transaction.begin()
      val managedDS = em.find(AggregatedWeatherDataSet::class.java, ds.key)
      managedDS.dailyRain = ds.dailyRain
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun removeDataset(ds: SmoothedWeatherDataSet) {
    val em = factory.createEntityManager()

    try {
      em.transaction.begin()
      val managedDS = em.find(SmoothedWeatherDataSet::class.java, ds.key)
      em.remove(managedDS)
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun makePersitant(dataSet: AggregatedWeatherDataSet) {
    ObjectifyService.ofy().save().entity(dataSet).now()
  }

  fun fetchDataSetMinutesBefore(referenceDate: Date, minutes: Int): SmoothedWeatherDataSet? {
    val minAge = DateUtil.minutesBefore(referenceDate, minutes)
    val maxAge = DateUtil.minutesBefore(referenceDate, minutes + 30)

    val em = factory.createEntityManager()
    try {
      val q = em.createQuery(
          "SELECT wds FROM SmoothedWeatherDataSet wds WHERE wds.timestamp < :minAge AND  wds.timestamp > :maxAge ORDER by wds.timestamp desc")
      q.setParameter("minAge", minAge, TemporalType.DATE)
      q.setParameter("maxAge", maxAge, TemporalType.DATE)


      val result = selectFirstSmoothedResult(q)
      log.info("found dataset: " + result?.timestamp)
      return result
    } finally {
      em.close()
    }
  }

  fun fetchYoungestDataSet(): WeatherDataSet? = ObjectifyService.ofy()
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

  fun fetchSmoothedWeatherDataInRange(begin: Date, end: Date?): MutableList<SmoothedWeatherDataSet>  =
    ObjectifyService.ofy().load()
      .type(SmoothedWeatherDataSet::class.java)
      .filter("timestamp >=", begin)
      .filter("timestamp <=", end)
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
      .filter("time >=", begin)
      .filter("time <=", end)
      .list()
      .map { it.id }

    ObjectifyService.ofy().delete().type(WeatherDataSet::class.java).ids(ids).now()
  }

  fun fetchImageIdentifiers(): List<ImageIdentifier> {
    val em = factory.createEntityManager()
    try {
      val q = em.createQuery("SELECT id FROM ImageIdentifier id")
      return (q.resultList as List<ImageIdentifier>).toList()
    } finally {
      em.close()
    }
  }

  fun fetchLocationProperties(): LocationProperties {
    val em = factory.createEntityManager()
    try {
      val q = em.createQuery("SELECT lp FROM LocationProperties lp")
      q.maxResults = 1
      return q.singleResult as LocationProperties
    } finally {
      em.close()
    }
  }

  private fun createQueryForRange(dataType: String, begin: Date, end: Date?, em: EntityManager): Query {
    val q: Query
    if (end != null) {
      q = em.createQuery("SELECT wds FROM " + dataType
                             + " wds WHERE wds.timestamp >= :begin and wds.timestamp <= :end ORDER by wds.timestamp")
      q.setParameter("begin", begin, TemporalType.DATE)
      q.setParameter("end", end, TemporalType.DATE)
    } else {
      q = em.createQuery("SELECT wds FROM $dataType wds WHERE wds.timestamp >= :begin ORDER by wds.timestamp")
      q.setParameter("begin", begin, TemporalType.DATE)
    }
    return q
  }

  private fun selectFirstResult(q: Query): WeatherDataSet? {
    q.maxResults = 1
    val results = q.resultList
    return if (results.size > 0) {
      results[0] as WeatherDataSet
    } else null
  }


  private fun selectFirstSmoothedResult(q: Query): SmoothedWeatherDataSet? {
    val list = selectNumberOfSmoothedResult(q, 1)
    return if (list.isNotEmpty()) {
      list[0]
    } else null
  }

  private fun selectNumberOfSmoothedResult(q: Query, number: Int): List<SmoothedWeatherDataSet> {
    q.maxResults = number
    return (q.resultList as List<SmoothedWeatherDataSet>).toList()
  }

  private fun selectFirstAggregatedResult(q: Query): AggregatedWeatherDataSet? {
    q.maxResults = 1
    val results = q.resultList
    return if (results.size > 0) {
      results[0] as AggregatedWeatherDataSet
    } else null
  }

  fun selectHealth(day: Date): Health {
    val em = factory.createEntityManager()
    return selectHealth(em, day)
  }

  private fun selectHealth(em: EntityManager, day: Date): Health {
    val q = em.createQuery("SELECT h from  Health h where h.day = :day")
    q.setParameter("day", day, TemporalType.DATE)
    val resultList = q.resultList
    return if (resultList.size > 0) {
      resultList[0] as Health
    } else Health(day)
  }

  fun makePersistent(dto: HealthDTO) {
    val em = factory.createEntityManager()
    try {
      val health = selectHealth(em, dto.day)
      health.fromDTO(dto)
      em.transaction.begin()
      em.persist(health)
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun makePersistent(contact: Contact) {
    val em = factory.createEntityManager()
    try {
      em.transaction.begin()
      em.persist(contact)
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun fetchContacts(): List<Contact> {
    val em = factory.createEntityManager()
    val q = em.createQuery("SELECT c from Contact c")
    return (q.resultList as List<Contact>).toList()
  }

}

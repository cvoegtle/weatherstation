package org.voegtle.weatherstation.server.persistence

import org.voegtle.weatherstation.server.image.Image
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.Contact
import org.voegtle.weatherstation.server.persistence.entities.Health
import org.voegtle.weatherstation.server.persistence.entities.ImageIdentifier
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.WeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.WeatherLocation
import org.voegtle.weatherstation.server.util.DateUtil
import java.util.Date
import java.util.HashMap
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.persistence.Persistence
import javax.persistence.Query
import javax.persistence.TemporalType

class PersistenceManager {
  companion object {
    private val PERSISTENCE_UNIT_NAME = "transactions-optional"
    private val factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)

    private val log = Logger.getLogger(PersistenceManager::class.java.name)
  }

  fun makePersitant(dataSet: WeatherDataSet): Boolean {
    if (dataSet.isValid) {
      val em = factory.createEntityManager()
      try {
        em.transaction.begin()
        em.persist(dataSet)
        em.transaction.commit()
      } finally {
        em.close()
      }
    }

    return dataSet.isValid
  }

  fun makePersitant(dataSet: SmoothedWeatherDataSet) {
    val em = factory.createEntityManager()

    try {
      em.transaction.begin()
      em.persist(dataSet)
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun makePersistant(id: ImageIdentifier) {
    val em = factory.createEntityManager()

    try {
      em.transaction.begin()
      em.persist(id)
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun makePersistant(lp: LocationProperties): Boolean {
    if (lp.isValid) {
      val em = factory.createEntityManager()
      try {
        em.transaction.begin()
        em.persist(lp)
        em.transaction.commit()
      } finally {
        em.close()

      }
    }

    return lp.isValid
  }

  fun makePersistant(location: WeatherLocation) {
    val em = factory.createEntityManager()
    try {
      em.transaction.begin()
      em.persist(location)
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun makePersistant(image: Image) {
    val em = factory.createEntityManager()
    try {
      em.transaction.begin()
      em.persist(image)
      em.transaction.commit()
    } finally {
      em.close()
    }
  }

  fun clearImages() {
    val em = factory.createEntityManager()

    em.createQuery("DELETE FROM Image img").executeUpdate()

    em.close()
  }

  fun countImages(): Int {
    val em = factory.createEntityManager()

    val q = em.createQuery("SELECT count(img.oid) FROM Image img")
    val count = q.singleResult as Long?
    return (count ?: 0).toInt()
  }


  fun fetchImage(oid: String): Image? {
    var result: Image? = null

    val em = factory.createEntityManager()
    try {
      val q = em.createQuery("SELECT img FROM Image img WHERE img.oid = :oid")
      q.setParameter("oid", oid)
      q.maxResults = 1
      val it = q.resultList.listIterator()
      if (it.hasNext()) {
        result = it.next() as Image
      }
    } finally {
      em.close()
    }

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
    val em = factory.createEntityManager()
    try {
      em.transaction.begin()
      em.persist(dataSet)
      em.transaction.commit()
    } finally {
      em.close()
    }
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

  fun fetchYoungestDataSet(): WeatherDataSet? {
    val em = factory.createEntityManager()
    try {
      val q = em.createQuery("SELECT wds FROM WeatherDataSet wds ORDER by wds.timestamp DESC")
      return selectFirstResult(q)
    } finally {
      em.close()
    }
  }

  fun fetchWeatherDataInRange(begin: Date, end: Date): List<WeatherDataSet> {
    val em = factory.createEntityManager()

    try {
      val q = createQueryForRange("WeatherDataSet", begin, end, em)
      return (q.resultList as List<WeatherDataSet>).toList()
    } finally {
      em.close()
    }
  }

  fun fetchSmoothedWeatherDataInRange(begin: Date, end: Date?): MutableList<SmoothedWeatherDataSet> {
    val em = factory.createEntityManager()
    try {
      val q = createQueryForRange("SmoothedWeatherDataSet", begin, end, em)
      return (q.resultList as List<SmoothedWeatherDataSet>).toMutableList()
    } finally {
      em.close()
    }
  }


  fun fetchAggregatedWeatherDataInRange(begin: Date, end: Date?): List<AggregatedWeatherDataSet> {
    return fetchAggregatedWeatherDataInRange(begin, end, true)
  }

  fun fetchAggregatedWeatherDataInRange(begin: Date, end: Date?, ascending: Boolean): List<AggregatedWeatherDataSet> {
    val em = factory.createEntityManager()
    try {
      val q: Query
      if (end != null) {
        q = em.createQuery(
            "SELECT wds FROM AggregatedWeatherDataSet wds WHERE wds.date >= :begin and wds.date <= :end ORDER by wds.date " + if (ascending) "" else "DESC")
        q.setParameter("begin", begin, TemporalType.DATE)
        q.setParameter("end", end, TemporalType.DATE)
      } else {
        q = em.createQuery(
            "SELECT wds FROM AggregatedWeatherDataSet wds WHERE wds.date >= :begin ORDER by wds.date" + if (ascending) "" else "DESC")
        q.setParameter("begin", begin, TemporalType.DATE)
      }

      return (q.resultList as List<AggregatedWeatherDataSet>).toList()
    } finally {
      em.close()
    }
  }

  fun fetchOldestSmoothedDataSetInRange(begin: Date, end: Date): SmoothedWeatherDataSet? {
    val em = factory.createEntityManager()
    val q = em
        .createQuery(
            "SELECT wds FROM SmoothedWeatherDataSet wds  WHERE wds.timestamp >= :begin and wds.timestamp <= :end ORDER by wds.timestamp")
    q.setParameter("begin", begin, TemporalType.DATE)
    q.setParameter("end", end, TemporalType.DATE)
    val result = selectFirstSmoothedResult(q)
    em.close()

    return result
  }

  fun fetchYoungestSmoothedDataSet(): SmoothedWeatherDataSet? {
    val em = factory.createEntityManager()
    val q = em.createQuery("SELECT wds FROM SmoothedWeatherDataSet wds ORDER by wds.timestamp DESC")
    val result = selectFirstSmoothedResult(q)
    em.close()

    return result
  }

  fun fetchYoungestAggregatedDataSet(period: PeriodEnum): AggregatedWeatherDataSet? {
    val em = factory.createEntityManager()
    val q = em.createQuery(
        "SELECT wds FROM AggregatedWeatherDataSet wds WHERE wds.period = :periodEnum ORDER by wds.date DESC")
    q.setParameter("periodEnum", period)
    val result = selectFirstAggregatedResult(q)
    em.close()

    return result
  }

  fun removeWeatherDataInRange(begin: Date, end: Date) {
    val em = factory.createEntityManager()
    val q = em.createQuery("DELETE FROM WeatherDataSet wds WHERE wds.timestamp >= :begin and wds.timestamp <= :end")
    q.setParameter("begin", begin, TemporalType.DATE)
    q.setParameter("end", end, TemporalType.DATE)
    q.executeUpdate()

    em.close()
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

  fun makePersistant(dto: HealthDTO) {
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

  fun makePersistant(contact: Contact) {
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

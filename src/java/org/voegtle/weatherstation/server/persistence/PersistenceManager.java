package org.voegtle.weatherstation.server.persistence;

import org.voegtle.weatherstation.server.image.Image;
import org.voegtle.weatherstation.server.util.DateUtil;

import javax.persistence.*;
import java.util.*;
import java.util.logging.Logger;

public class PersistenceManager {
  private static final String PERSISTENCE_UNIT_NAME = "transactions-optional";
  private static final EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

  public PersistenceManager() {
  }

  public boolean makePersitant(WeatherDataSet dataSet) {
    boolean persisted = false;
    EntityManager em = factory.createEntityManager();

    if (dataSet.isValid()) {
      em.getTransaction().begin();
      em.persist(dataSet);
      em.getTransaction().commit();
      persisted = true;
    }

    em.close();
    return persisted;
  }

  public void makePersitant(SmoothedWeatherDataSet dataSet) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    em.persist(dataSet);
    em.getTransaction().commit();

    em.close();
  }

  public void makePersistant(ImageIdentifier id) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    em.persist(id);
    em.getTransaction().commit();

    em.close();
  }

  public boolean makePersistant(LocationProperties lp) {
    boolean persisted = false;
    EntityManager em = factory.createEntityManager();

    if (lp.isValid()) {
      em.getTransaction().begin();
      em.persist(lp);
      em.getTransaction().commit();
      persisted = true;
    }

    em.close();
    return persisted;
  }

  public void makePersistant(WeatherLocation location) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    em.persist(location);
    em.getTransaction().commit();

    em.close();
  }

  public void makePersistant(Image image) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    em.persist(image);
    em.getTransaction().commit();

    em.close();
  }

  public void clearImages() {
    EntityManager em = factory.createEntityManager();

    em.createQuery("DELETE FROM Image").executeUpdate();

    em.close();
  }

  public int countImages() {
    EntityManager em = factory.createEntityManager();

    Query q = em.createQuery("SELECT count(img.oid) FROM Image img");
    Integer count = (Integer) q.getSingleResult();
    return count != null ? count : 0;
  }


  public Image fetchImage(String oid) {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT img FROM Image img WHERE img.oid = :oid");
    q.setParameter("oid", oid);
    q.setMaxResults(1);
    ListIterator it = q.getResultList().listIterator();
    Image result = null;
    if (it.hasNext()) {
      result = (Image) it.next();
    }
    em.close();
    return result;
  }


  public HashMap<String, WeatherLocation> fetchWeatherLocations() {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT location FROM WeatherLocation location");

    HashMap<String, WeatherLocation> result = new HashMap<>();
    for (WeatherLocation location : (List<WeatherLocation>) (q.getResultList())) {
      result.put(location.getLocation(), location);
    }

    em.close();
    return result;
  }

  public void updateDataset(SmoothedWeatherDataSet ds) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    SmoothedWeatherDataSet managedDS = em.find(SmoothedWeatherDataSet.class, ds.getKey());
    managedDS.setOutsideHumidity(ds.getOutsideHumidity());
    managedDS.setOutsideTemperature(ds.getOutsideTemperature());
    managedDS.setRainCounter(ds.getRainCounter());
    managedDS.setRaining(ds.isRaining());
    managedDS.setInsideTemperature(ds.getInsideTemperature());
    managedDS.setInsideHumidity(ds.getInsideHumidity());
    managedDS.setKwh(ds.getKwh());
    managedDS.setRepaired(ds.getRepaired());
    em.getTransaction().commit();

    em.close();
  }

  public void removeDataset(SmoothedWeatherDataSet ds) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    SmoothedWeatherDataSet managedDS = em.find(SmoothedWeatherDataSet.class, ds.getKey());
    em.remove(managedDS);
    em.getTransaction().commit();

    em.close();
  }

  public void makePersitant(AggregatedWeatherDataSet dataSet) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    em.persist(dataSet);
    em.getTransaction().commit();

    em.close();
  }

  private static final Logger log = Logger.getLogger(PersistenceManager.class.getName());
  public void makePersistant(CacheWeatherDTO cacheWeatherDTO) {
    EntityManager em = factory.createEntityManager();

    CacheWeatherDTO existingDTO = em.find(CacheWeatherDTO.class, cacheWeatherDTO.getId());
    em.getTransaction().begin();
    if (existingDTO == null) {
      log.info("create new  CacheWeatherDTO");
      em.persist(cacheWeatherDTO);
    } else {
      log.info("updating existing CacheWeatherDTO");
      existingDTO.copyFrom(cacheWeatherDTO);
    }
    em.getTransaction().commit();

    em.close();
  }

  public SmoothedWeatherDataSet fetchDataSetMinutesBefore(Date referenceDate, int minutes) {
    Calendar minAge = DateUtil.minutesBefore(referenceDate, minutes);
    Calendar maxAge = DateUtil.minutesBefore(referenceDate, minutes+30);

    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT wds FROM SmoothedWeatherDataSet wds WHERE wds.timestamp < :minAge AND  wds.timestamp > :maxAge ORDER by wds.timestamp desc");
    q.setParameter("minAge", minAge, TemporalType.DATE);
    q.setParameter("maxAge", maxAge, TemporalType.DATE);


    SmoothedWeatherDataSet result = selectFirstSmoothedResult(q);
    log.info("found dataset: " + (result == null ? null : result.getTimestamp()));
    em.close();

    return result;
  }

  public WeatherDataSet fetchYoungestDataSet() {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT wds FROM WeatherDataSet wds ORDER by wds.timestamp DESC");
    WeatherDataSet result = selectFirstResult(q);
    em.close();
    return result;
  }

  @SuppressWarnings("unchecked")
  public List<WeatherDataSet> fetchWeatherDataInRange(Date begin, Date end) {
    EntityManager em = factory.createEntityManager();
    Query q = createQueryForRange("WeatherDataSet", begin, end, em);

    ArrayList<WeatherDataSet> result = new ArrayList<>();
    for (WeatherDataSet wds : (List<WeatherDataSet>) (q.getResultList())) {
      result.add(wds);
    }
    em.close();
    return result;
  }

  @SuppressWarnings("unchecked")
  public List<SmoothedWeatherDataSet> fetchSmoothedWeatherDataInRange(Date begin, Date end) {
    EntityManager em = factory.createEntityManager();
    Query q = createQueryForRange("SmoothedWeatherDataSet", begin, end, em);
    ArrayList<SmoothedWeatherDataSet> result = new ArrayList<>();
    for (SmoothedWeatherDataSet ds : (List<SmoothedWeatherDataSet>) (q.getResultList())) {
      result.add(ds);
    }
    em.close();
    return result;
  }


  public List<AggregatedWeatherDataSet> fetchAggregatedWeatherDataInRange(Date begin, Date end) {
    return fetchAggregatedWeatherDataInRange(begin, end, true);
  }

  @SuppressWarnings("unchecked")
  public List<AggregatedWeatherDataSet> fetchAggregatedWeatherDataInRange(Date begin, Date end, boolean ascending) {
    EntityManager em = factory.createEntityManager();
    Query q;
    if (end != null) {
      q = em.createQuery("SELECT wds FROM AggregatedWeatherDataSet wds WHERE wds.date >= :begin and wds.date <= :end ORDER by wds.date " + (ascending ? "" : "DESC"));
      q.setParameter("begin", begin, TemporalType.DATE);
      q.setParameter("end", end, TemporalType.DATE);
    } else {
      q = em.createQuery("SELECT wds FROM AggregatedWeatherDataSet wds WHERE wds.date >= :begin ORDER by wds.date" + (ascending ? "" : "DESC"));
      q.setParameter("begin", begin, TemporalType.DATE);
    }

    ArrayList<AggregatedWeatherDataSet> result = new ArrayList<>();
    for (AggregatedWeatherDataSet ds : (List<AggregatedWeatherDataSet>) (q.getResultList())) {
      result.add(ds);
    }
    em.close();
    return result;
  }

  public SmoothedWeatherDataSet fetchOldestSmoothedDataSetInRange(Date begin, Date end) {
    EntityManager em = factory.createEntityManager();
    Query q = em
        .createQuery("SELECT wds FROM SmoothedWeatherDataSet wds  WHERE wds.timestamp >= :begin and wds.timestamp <= :end ORDER by wds.timestamp");
    q.setParameter("begin", begin, TemporalType.DATE);
    q.setParameter("end", end, TemporalType.DATE);
    SmoothedWeatherDataSet result = selectFirstSmoothedResult(q);
    em.close();

    return result;
  }

  public SmoothedWeatherDataSet fetchYoungestSmoothedDataSet() {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT wds FROM SmoothedWeatherDataSet wds ORDER by wds.timestamp DESC");
    SmoothedWeatherDataSet result = selectFirstSmoothedResult(q);
    em.close();

    return result;
  }

  public AggregatedWeatherDataSet fetchYoungestAggregatedDataSet(PeriodEnum period) {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT wds FROM AggregatedWeatherDataSet wds WHERE wds.period = :periodEnum ORDER by wds.date DESC");
    q.setParameter("periodEnum", period);
    AggregatedWeatherDataSet result = selectFirstAggregatedResult(q);
    em.close();

    return result;
  }

  public void removeWeatherDataInRange(Date begin, Date end) {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("DELETE FROM WeatherDataSet wds WHERE wds.timestamp >= :begin and wds.timestamp <= :end");
    q.setParameter("begin", begin, TemporalType.DATE);
    q.setParameter("end", end, TemporalType.DATE);
    q.executeUpdate();

    em.close();
  }

  public List<ImageIdentifier> fetchImageIdentifiers() {
    ArrayList<ImageIdentifier> result = new ArrayList<>();
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT id FROM ImageIdentifier id");
    List<ImageIdentifier> list = (List<ImageIdentifier>) (q.getResultList());
    for (ImageIdentifier id : list) {
      result.add(id);
    }
    em.close();
    return result;

  }

  public LocationProperties fetchLocationProperties() {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT lp FROM LocationProperties lp");
    q.setMaxResults(1);
    LocationProperties result = (LocationProperties) q.getSingleResult();

    em.close();
    return result;
  }

  private Query createQueryForRange(String dataType, Date begin, Date end, EntityManager em) {
    Query q;
    if (end != null) {
      q = em.createQuery("SELECT wds FROM " + dataType
          + " wds WHERE wds.timestamp >= :begin and wds.timestamp <= :end ORDER by wds.timestamp");
      q.setParameter("begin", begin, TemporalType.DATE);
      q.setParameter("end", end, TemporalType.DATE);
    } else {
      q = em.createQuery("SELECT wds FROM " + dataType + " wds WHERE wds.timestamp >= :begin ORDER by wds.timestamp");
      q.setParameter("begin", begin, TemporalType.DATE);
    }
    return q;
  }

  private WeatherDataSet selectFirstResult(Query q) {
    q.setMaxResults(1);
    @SuppressWarnings("rawtypes")
    List results = q.getResultList();
    if (results.size() > 0) {
      return (WeatherDataSet) results.get(0);
    }
    return null;
  }


  private SmoothedWeatherDataSet selectFirstSmoothedResult(Query q) {
    List<SmoothedWeatherDataSet> list = selectNumberOfSmoothedResult(q, 1);
    if (list.size()>0) {
      return list.get(0);
    }
    return null;
  }


  private List<SmoothedWeatherDataSet> selectNumberOfSmoothedResult(Query q, int number) {
    ArrayList<SmoothedWeatherDataSet> list = new ArrayList<>();
    q.setMaxResults(number);
    @SuppressWarnings("rawtypes")
    List results = q.getResultList();
    for (Object result : results) {
      list.add((SmoothedWeatherDataSet) result);
    }
    return list;
  }

  private AggregatedWeatherDataSet selectFirstAggregatedResult(Query q) {
    q.setMaxResults(1);
    @SuppressWarnings("rawtypes")
    List results = q.getResultList();
    if (results.size() > 0) {
      return (AggregatedWeatherDataSet) results.get(0);
    }
    return null;
  }

  public  HashMap<String, CacheWeatherDTO> fetchCacheWeatherDTO(List<String> locations) {
    HashMap<String, CacheWeatherDTO> result = new  HashMap<>();
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT cwd FROM CacheWeatherDTO cwd WHERE cwd.id IN (:locations)");
    q.setParameter("locations", locations);
    @SuppressWarnings("unchecked")
    List<CacheWeatherDTO> queryResult = (List<CacheWeatherDTO>)(q.getResultList());
    for (CacheWeatherDTO dto : queryResult){
      result.put(dto.getId(), dto);
    }
    return result;
  }

}

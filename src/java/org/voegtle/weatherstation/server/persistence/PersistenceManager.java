package org.voegtle.weatherstation.server.persistence;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

  public void updateDataset(SmoothedWeatherDataSet ds) {
    EntityManager em = factory.createEntityManager();

    em.getTransaction().begin();
    SmoothedWeatherDataSet managedDS = em.find(SmoothedWeatherDataSet.class, ds.getKey());
    managedDS.setOutsideHumidity(ds.getOutsideHumidity());
    managedDS.setOutsideTemperature(ds.getOutsideTemperature());
    managedDS.setRainCounter(ds.getRainCounter());
    managedDS.setRaining(ds.isRaining());
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

  public SmoothedWeatherDataSet fetchDataSetOneHourBefore(Date referenceDate) {
    Calendar oneHourBefore = Calendar.getInstance();
    if (referenceDate != null) {
      oneHourBefore.setTime(referenceDate);
    }

    oneHourBefore.add(Calendar.HOUR_OF_DAY, -1);

    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT wds FROM SmoothedWeatherDataSet wds WHERE wds.timestamp > :oneHourBefore ORDER by wds.timestamp");
    q.setParameter("oneHourBefore", oneHourBefore, TemporalType.DATE);

    return selectFirstSmoothedResult(q);
  }

  public WeatherDataSet fetchYoungestDataSet() {
    EntityManager em = factory.createEntityManager();
    Query q = em.createQuery("SELECT wds FROM WeatherDataSet wds ORDER by wds.timestamp DESC");
    return selectFirstResult(q);
  }

  @SuppressWarnings("unchecked")
  public List<WeatherDataSet> fetchWeatherDataInRange(Date begin, Date end) {
    EntityManager em = factory.createEntityManager();
    Query q = createQueryForRange("WeatherDataSet", begin, end, em);
    return (List<WeatherDataSet>) (q.getResultList());
  }

  @SuppressWarnings("unchecked")
  public List<SmoothedWeatherDataSet> fetchSmoothedWeatherDataInRange(Date begin, Date end) {
    EntityManager em = factory.createEntityManager();
    Query q = createQueryForRange("SmoothedWeatherDataSet", begin, end, em);
    return (List<SmoothedWeatherDataSet>) (q.getResultList());
  }


  @SuppressWarnings("unchecked")

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

    return (List<AggregatedWeatherDataSet>) (q.getResultList());
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
    q.setMaxResults(1);
    @SuppressWarnings("rawtypes")
    List results = q.getResultList();
    if (results.size() > 0) {
      return (SmoothedWeatherDataSet) results.get(0);
    }
    return null;
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

}

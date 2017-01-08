package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.persistence.Health;
import org.voegtle.weatherstation.server.persistence.HealthDTO;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.util.DateUtil;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HealthProvider {
  protected static final Logger log = Logger.getLogger("HealthLogger");
  private static final String HEALTH = "health";
  private Cache cache;
  private PersistenceManager pm;

  private DateUtil dateUtil;

  private Health health;

  public HealthProvider(PersistenceManager pm, LocationProperties locationProperties) {
    this.pm = pm;
    this.dateUtil = locationProperties.getDateUtil();
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      Map properties = new HashMap<>();
      cache = cacheFactory.createCache(properties);
    } catch (CacheException e) {
      log.severe("CentralServlet: Could not instantiate Cache");
    }
  }

  public HealthDTO get() {
    Date today = dateUtil.getToday();
    HealthDTO health = (HealthDTO)cache.get(HEALTH);
    if (health == null || isOutdated(health, today)) {
      health = pm.selectHealth(today).toDTO();
    }
    return health;
  }

  public void update(HealthDTO health) {
    cache.put(HEALTH, health);
    pm.makePersistant(health);
  }

  private boolean isOutdated(HealthDTO health, Date today) {
    return today.after(health.getDay());
  }
}

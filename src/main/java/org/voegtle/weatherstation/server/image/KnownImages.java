package org.voegtle.weatherstation.server.image;

import org.voegtle.weatherstation.server.persistence.entities.ImageIdentifier;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class KnownImages {
  PersistenceManager pm;
  HashMap<String, ImageIdentifier> identifiers = new HashMap<>();

  public KnownImages(PersistenceManager pm) {
    this.pm = pm;
  }

  public void init() {
    List<ImageIdentifier> imageIdentifiers = pm.fetchImageIdentifiers();
    for (ImageIdentifier id : imageIdentifiers) {
      identifiers.put(id.getOid(), id);
    }
  }

  public void put(ImageIdentifier identifier) {
    if (identifiers.put(identifier.getOid(), identifier) == null) {
      pm.makePersistant(identifier);
    }
  }

  public Collection<ImageIdentifier> values() {
    return identifiers.values();
  }

}

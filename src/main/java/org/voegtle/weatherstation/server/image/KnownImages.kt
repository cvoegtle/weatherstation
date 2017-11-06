package org.voegtle.weatherstation.server.image

import org.voegtle.weatherstation.server.persistence.entities.ImageIdentifier
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import java.util.HashMap

class KnownImages(internal var pm: PersistenceManager) {
  internal var identifiers = HashMap<String, ImageIdentifier>()

  fun init() {
    val imageIdentifiers = pm.fetchImageIdentifiers()
    for (id in imageIdentifiers) {
      identifiers.put(id.oid, id)
    }
  }

  fun put(identifier: ImageIdentifier) {
    if (identifiers.put(identifier.oid, identifier) == null) {
      pm.makePersistant(identifier)
    }
  }

  fun values(): Collection<ImageIdentifier> {
    return identifiers.values
  }

}

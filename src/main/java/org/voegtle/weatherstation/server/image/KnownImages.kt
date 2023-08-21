package org.voegtle.weatherstation.server.image

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.ImageIdentifier

class KnownImages(internal var pm: PersistenceManager) {
  internal var identifiers = HashMap<String, ImageIdentifier>()

  fun init() {
    val imageIdentifiers = pm.fetchImageIdentifiers()
    for (id in imageIdentifiers) {
      id.oid?.let {
        identifiers.put(it, id)
      }
    }
  }

  fun put(identifier: ImageIdentifier) {
    identifier.oid?.let {
      if (identifiers.put(it, identifier) == null) {
        pm.makePersistent(identifier)
      }
    }
  }

  fun values(): Collection<ImageIdentifier> {
    return identifiers.values
  }

}

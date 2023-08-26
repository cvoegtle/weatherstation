package org.voegtle.weatherstation.server.image

import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.ImageIdentifier
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.logging.Logger

class ImageCache(private val pm: PersistenceManager) {
  private val log = Logger.getLogger("ImageCacheLogger")

  private val imageServers = ImageServers()
  private val TIMEOUT = 10000
  private val knownImages = KnownImages(pm)

  fun init() {
    knownImages.init()
  }

  fun clear() {
    pm.clearImages()
  }

  @Throws(IOException::class)
  fun refresh(begin: Int?, end: Int?) {
    var index = 0
    for (id in knownImages.values()) {
      if (begin == null || index.compareTo(begin) >= 0) {
        get(id, true)
      }
      if (index == end) {
        break
      }
      index++
    }
  }

  operator fun get(identifier: ImageIdentifier): Image? {
    return get(identifier, false)
  }

  private operator fun get(identifier: ImageIdentifier, forceReload: Boolean): Image? {
    var image = pm.fetchImage(identifier.oid!!)
    if (image == null || image.isOld || forceReload) {
      image = fetchImageRepeated(identifier)
      if (image != null) {
        knownImages.put(identifier)
        pm.makePersistent(image)
      }
    }
    return image
  }

  private fun fetchImageRepeated(identifier: ImageIdentifier): Image? {
    log.info("fetch image " + identifier.oid + ", " + identifier.zx)
    var image: Image? = null
    try {
      image = fetch(identifier)
    } catch (e: IOException) {
      // einmal wiederholen
      try {
        Thread.sleep(1500)
        image = fetch(identifier)
        log.info("repeated image fetch " + identifier.oid + ", " + identifier.zx + "\n" + e.message)
      } catch (e1: IOException) {
        log.info("failed to fetch image " + identifier.oid + ", " + identifier.zx + "\n" + e1.message)
      } catch (e1: InterruptedException) {
        log.info("failed to fetch image " + identifier.oid + ", " + identifier.zx + "\n" + e1.message)
      }

    }

    return image
  }

  @Throws(IOException::class)
  private fun fetch(identifier: ImageIdentifier): Image {
    val imageServerUrl = imageServers.get(identifier.sheet)

    val url = URL(imageServerUrl + identifier.asUrlParameter())
    val connection = url.openConnection()
    connection.connectTimeout = TIMEOUT
    val input = connection.getInputStream()

    val buffer = ByteArray(4096)
    val bos = ByteArrayOutputStream()
    var read = input.read(buffer)
    while (read > 0) {
      bos.write(buffer, 0, read)
      read = input.read(buffer)
    }

    return Image(identifier.oid, Date(), bos.toByteArray())
  }

  fun size(): Int {
    return pm.countImages()
  }
}

package org.voegtle.weatherstation.server.image;

import org.voegtle.weatherstation.server.persistence.ImageIdentifier;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.logging.Logger;

public class ImageCache {
  protected static final Logger log = Logger.getLogger("ImageCacheLogger");

  private static final ImageServers imageServers = new ImageServers();
  private static final int TIMEOUT = 10000;

  private final PersistenceManager pm;

  private KnownImages knownImages;

  public ImageCache(PersistenceManager pm) {
    knownImages = new KnownImages(pm);
    this.pm = pm;
  }

  public void init() {
    knownImages.init();

  }

  public void clear() {
    pm.clearImages();
  }

  public void refresh(Integer begin, Integer end) throws IOException {
    Integer index = 0;
    for (ImageIdentifier id : knownImages.values()) {
      if (begin == null || index.compareTo(begin) >= 0) {
        get(id, true);
      }
      if (index.equals(end)) {
        break;
      }
      index++;
    }
  }

  public Image get(ImageIdentifier identifier) {
    return get(identifier, false);
  }

  private Image get(ImageIdentifier identifier, boolean forceReload) {
    Image image = pm.fetchImage(identifier.getOid());
    if (image == null || image.isOld() || forceReload) {
      image = fetchImageRepeated(identifier);
      if (image != null) {
        knownImages.put(identifier);
        pm.makePersistant(image);
      }
    }
    return image;
  }

  private Image fetchImageRepeated(ImageIdentifier identifier) {
    log.info("fetch image " + identifier.getOid() + ", " + identifier.getZx());
    Image image = null;
    try {
      image = fetch(identifier);
    } catch (IOException e) {
      // einmal wiederholen
      try {
        Thread.sleep(1500);
        image = fetch(identifier);
        log.info("repeated image fetch " + identifier.getOid() + ", " + identifier.getZx() + "\n" + e.getMessage());
      } catch (IOException | InterruptedException e1) {
        log.info("failed to fetch image " + identifier.getOid() + ", " + identifier.getZx() + "\n" + e1.getMessage());
      }
    }
    return image;
  }

  private Image fetch(ImageIdentifier identifier) throws IOException {
    String imageServerUrl = imageServers.get(identifier.getSheet());

    URL url = new URL(imageServerUrl + identifier.asUrlParameter());
    URLConnection connection = url.openConnection();
    connection.setConnectTimeout(TIMEOUT);
    InputStream in = connection.getInputStream();

    byte[] buffer = new byte[4096];
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int read;
    while ((read = in.read(buffer)) > 0) {
      bos.write(buffer, 0, read);
    }

    return new Image(identifier.getOid(), new Date(), bos.toByteArray());
  }

  public int size() {
    return pm.countImages();
  }

//  public static void main(String[] args) {
//    try {
//      ImageCache cache = new ImageCache();
//      Image image = cache.get("0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=23&zx=juk5ebnhgov3");
//      FileOutputStream fos = new FileOutputStream("test.png");
//      fos.write(image.getPngAsBytes());
//      fos.close();
//    } catch (IOException ex) {
//
//    }
//
//  }
}

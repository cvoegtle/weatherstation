package org.voegtle.weatherstation.server.image;

import org.voegtle.weatherstation.server.persistence.ImageIdentifier;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

public class ImageCache {
  protected static final Logger log = Logger.getLogger("ImageCacheLogger");

  private static final String imageServerUrl = "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc";

  private HashMap<String, Image> images = new HashMap<>();
  private KnownImages knownImages;

  public ImageCache(PersistenceManager pm) {
    knownImages = new KnownImages(pm);
  }

  public void init() {
    knownImages.init();
  }

  public void refresh() throws IOException {
    images.clear();
    for (ImageIdentifier id : knownImages.values()) {
      get(id);
    }
  }

  public Image get(ImageIdentifier identifier) {
    Image image = images.get(identifier.getOid());
    if (image == null || image.isOld()) {
      image = fetchImageRepeated(identifier);
      if (image != null) {
        images.put(image.getOid(), image);
        knownImages.put(identifier);
      }
    }
    return image;
  }

  private Image fetchImageRepeated(ImageIdentifier identifier) {
    Image image = null;
    try {
      image = fetch(identifier);
    } catch (IOException e) {
      // einmal wiederholen
      try {
        Thread.sleep(1500);
        image = fetch(identifier);
        log.info("repeated image fetch " + identifier.getOid() + ", " + identifier.getZx());
      } catch (IOException | InterruptedException e1) {
        log.info("failed to fetch image " + identifier.getOid() + ", " + identifier.getZx());
      }
    }
    return image;
  }

  private Image fetch(ImageIdentifier identifier) throws IOException {
    URL url = new URL(imageServerUrl + identifier.asUrlParameter());
    InputStream in = url.openStream();

    byte[] buffer = new byte[4096];
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int read;
    while ((read = in.read(buffer)) > 0) {
      bos.write(buffer, 0, read);
    }

    return new Image(identifier.getOid(), new Date(), bos.toByteArray());
  }

  public int size() {
    return images.size();
  }

//  public static void main(String[] args) {
//    try {
//      ImageCache cache = new ImageCache();
//      Image image = cache.get("0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc&oid=23&zx=juk5ebnhgov3");
//      FileOutputStream fos = new FileOutputStream("test.png");
//      fos.write(image.getPng());
//      fos.close();
//    } catch (IOException ex) {
//
//    }
//
//  }
}

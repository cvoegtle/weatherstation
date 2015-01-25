package org.voegtle.weatherstation.server.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

public class ImageCache {
  protected static final Logger log = Logger.getLogger("ImageCacheLogger");

  private static final String imageServerUrl = "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc";

  private HashMap<String, Image> images = new HashMap<>();
  private ArrayList<ImageIdentifier> knownImages = new ArrayList<>();

  public ImageCache() {
    knownImages.add(new ImageIdentifier("22", "itnq9cg9itj1"));
    knownImages.add(new ImageIdentifier("23", "juk5ebnhgov3"));
    knownImages.add(new ImageIdentifier("26", "3goxceuvpnz7"));
    knownImages.add(new ImageIdentifier("24", "bmq3fhig2c"));
    knownImages.add(new ImageIdentifier("3", "jfy3wnfa5exa"));
    knownImages.add(new ImageIdentifier("4", "oqwhqpkqpxqq"));
    knownImages.add(new ImageIdentifier("21", "bju20lesmatj"));
    knownImages.add(new ImageIdentifier("7", "1geb1qiwcx3b"));
    knownImages.add(new ImageIdentifier("16", "v9yov3sfksqb"));
    knownImages.add(new ImageIdentifier("25", "3l67kc4xl7vx"));
  }

  public void refresh() throws IOException {
    images.clear();
    for (ImageIdentifier id : knownImages) {
      get(id);
    }
  }

  public Image get(ImageIdentifier identifier) {
    Image image = images.get(identifier.getOid());
    if (image == null || image.isOld()) {
      image = fetchImageRepeated(identifier);
      if (image != null) {
        images.put(image.getOid(), image);
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

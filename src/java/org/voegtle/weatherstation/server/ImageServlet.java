package org.voegtle.weatherstation.server;

import org.voegtle.weatherstation.server.image.Image;
import org.voegtle.weatherstation.server.image.ImageCache;
import org.voegtle.weatherstation.server.persistence.ImageIdentifier;
import org.voegtle.weatherstation.server.request.ImageUrlParameter;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImageServlet extends AbstractServlet {
  private ImageCache imageCache;

  @Override
  public void init() throws ServletException {
    super.init();
    imageCache = new ImageCache(pm);
    imageCache.init();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    ImageUrlParameter param = new ImageUrlParameter(request);
    if (param.isRefresh()) {
      imageCache.refresh(param.getBegin(), param.getEnd());
      response.getWriter().write("ACK " + imageCache.size());
      response.getWriter().close();
    } else if (param.isClear()) {
      imageCache.clear();
      response.getWriter().write("ACK");
      response.getWriter().close();
    } else {
      if (StringUtil.isNotEmpty(param.getOid())) {
        ImageIdentifier identifier;
        if (StringUtil.isNotEmpty(param.getZx())) {
          identifier = new ImageIdentifier(param.getOid(), param.getZx());
        } else {
          identifier = new ImageIdentifier(param.getSheet(), param.getOid(), param.getFormat());
        }
        Image image = imageCache.get(identifier);

        if (image != null) {
          response.setContentType("image/png");
          response.getOutputStream().write(image.getPngAsBytes());
          response.getOutputStream().close();
        }
      }
    }

  }


}

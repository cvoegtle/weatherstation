package org.voegtle.weatherstation.server;

import org.voegtle.weatherstation.server.image.Image;
import org.voegtle.weatherstation.server.image.ImageCache;
import org.voegtle.weatherstation.server.image.ImageIdentifier;
import org.voegtle.weatherstation.server.request.ImageUrlParameter;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImageServlet extends HttpServlet {
  private ImageCache imageCache = new ImageCache();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    ImageUrlParameter param = new ImageUrlParameter(request);
    if (param.isRefresh()) {
      imageCache.refresh();
      response.getWriter().write("ACK " + imageCache.size());
      response.getWriter().close();
    } else {
      if (StringUtil.isNotEmpty(param.getOid())) {
        Image image = imageCache.get(new ImageIdentifier(param.getOid(), param.getZx()));

        if (image != null) {
          response.setContentType("image/png");
          response.getOutputStream().write(image.getPng());
          response.getOutputStream().close();
        }
      }
    }

  }


}

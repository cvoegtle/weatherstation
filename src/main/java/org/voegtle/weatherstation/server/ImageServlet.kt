package org.voegtle.weatherstation.server

import org.voegtle.weatherstation.server.image.ImageCache
import org.voegtle.weatherstation.server.persistence.entities.ImageIdentifier
import org.voegtle.weatherstation.server.request.ImageUrlParameter
import org.voegtle.weatherstation.server.util.StringUtil
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ImageServlet : AbstractServlet() {
  private val imageCache = ImageCache(pm)

  @Throws(ServletException::class)
  override fun init() {
    super.init()
    imageCache.init()
  }

  @Throws(ServletException::class, IOException::class)
  override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {

    val param = ImageUrlParameter(request, locationProperties.dateUtil)
    if (param.isRefresh) {
      imageCache.refresh(param.begin, param.end)
      response.writer.write("ACK ${imageCache.size()}")
      response.writer.close()
    } else if (param.isClear) {
      imageCache.clear()
      response.writer.write("ACK")
      response.writer.close()
    } else {
      if (StringUtil.isNotEmpty(param.oid)) {
        val identifier = if (StringUtil.isNotEmpty(param.zx)) {
          ImageIdentifier(param.oid, param.zx)
        } else {
          ImageIdentifier(param.sheet, param.oid, param.format)
        }
        val image = imageCache[identifier]

        if (image != null) {
          response.contentType = "image/png"
          response.outputStream.write(image.pngAsBytes)
          response.outputStream.close()
        }
      }
    }

  }


}

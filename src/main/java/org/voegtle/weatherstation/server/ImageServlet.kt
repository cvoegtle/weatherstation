package org.voegtle.weatherstation.server

import org.voegtle.weatherstation.server.image.ImageCache
import org.voegtle.weatherstation.server.persistence.entities.ImageIdentifier
import org.voegtle.weatherstation.server.request.ImageUrlParameter
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ImageServlet : AbstractServlet() {
  private val imageCache = ImageCache(pm)

  @Throws(ServletException::class)
  override fun init() {
    super.init()
    runInObjectifyContext {
      val imageIdentifier = ImageIdentifier(1, "92028519")
      pm.makePersistent(imageIdentifier)

      imageCache.init()
    }
  }

  @Throws(ServletException::class, IOException::class)
  override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {

    val param = ImageUrlParameter(request, locationProperties!!.dateUtil)
    when {
      param.isRefresh -> {
        imageCache.refresh(param.begin, param.end)
        response.writer.write("ACK ${imageCache.size()}")
        response.writer.close()
      }
      param.isClear -> {
        imageCache.clear()
        response.writer.write("ACK")
        response.writer.close()
      }
      else -> param.oid?.let {
        val identifier = ImageIdentifier(param.sheet, it)

        val image = imageCache[identifier]

        image?.let {
          response.contentType = "image/png"
          response.outputStream.write(image.pngAsBytes)
          response.outputStream.close()
        }
      }
    }

  }


}

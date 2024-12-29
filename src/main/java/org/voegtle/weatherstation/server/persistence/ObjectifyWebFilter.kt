package org.voegtle.weatherstation.server.persistence

import com.googlecode.objectify.ObjectifyService
import jakarta.servlet.*
import jakarta.servlet.annotation.WebFilter
import java.io.IOException

@WebFilter(urlPatterns = ["/*"])
class ObjectifyWebFilter: Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        val closeable = ObjectifyService.begin()

        try {
            chain.doFilter(request, response)
        } catch (var8: Throwable) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (var7: Throwable) {
                    var8.addSuppressed(var7)
                }
            }

            throw var8
        }

        closeable?.close()
    }

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig?) {
    }

    override fun destroy() {
    }
}

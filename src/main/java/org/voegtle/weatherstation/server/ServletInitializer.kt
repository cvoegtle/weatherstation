package org.voegtle.weatherstation.server

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

class ServletInitializer : SpringBootServletInitializer() {
  override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
    return builder.sources(Application::class.java)
  }
}

import com.googlecode.objectify.ObjectifyFactory
import com.googlecode.objectify.ObjectifyService
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.voegtle.weatherstation.server.Application
import org.voegtle.weatherstation.server.persistence.entities.AggregatedWeatherDataSet
import org.voegtle.weatherstation.server.persistence.entities.Contact
import org.voegtle.weatherstation.server.persistence.entities.Health
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.weewx.SolarDataSet
import org.voegtle.weatherstation.server.weewx.WeewxDataSet

class ServletInitializer : SpringBootServletInitializer() {
  override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
    ObjectifyService.init(ObjectifyFactory())

    ObjectifyService.register(AggregatedWeatherDataSet::class.java)
    ObjectifyService.register(Contact::class.java)
    ObjectifyService.register(Health::class.java)
    ObjectifyService.register(LocationProperties::class.java)
    ObjectifyService.register(SmoothedWeatherDataSet::class.java)
    ObjectifyService.register(WeewxDataSet::class.java)
    ObjectifyService.register(SolarDataSet::class.java)

    return builder.sources(Application::class.java)
  }
}

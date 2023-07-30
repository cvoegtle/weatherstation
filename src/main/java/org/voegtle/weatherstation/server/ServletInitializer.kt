import com.googlecode.objectify.ObjectifyService
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.voegtle.weatherstation.server.Application
import org.voegtle.weatherstation.server.image.Image
import org.voegtle.weatherstation.server.persistence.entities.*

class ServletInitializer : SpringBootServletInitializer() {
    override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
        ObjectifyService.register(AggregatedWeatherDataSet::class.java)
        ObjectifyService.register(Contact::class.java)
        ObjectifyService.register(Health::class.java)
        ObjectifyService.register(LocationProperties::class.java)
        ObjectifyService.register(SmoothedWeatherDataSet::class.java)
        ObjectifyService.register(Image::class.java)
        ObjectifyService.register(WeatherLocation::class.java)

        return builder.sources(Application::class.java)
    }
}

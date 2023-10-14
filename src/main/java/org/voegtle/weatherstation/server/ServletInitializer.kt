import com.googlecode.objectify.ObjectifyFactory
import com.googlecode.objectify.ObjectifyService
import org.voegtle.weatherstation.server.persistence.entities.*

var classesRegistered = false
fun registerClassesForPersistence() {
    if (!classesRegistered) {
        ObjectifyService.init(ObjectifyFactory())
        ObjectifyService.register(AggregatedWeatherDataSet::class.java)
        ObjectifyService.register(Contact::class.java)
        ObjectifyService.register(Health::class.java)
        ObjectifyService.register(LocationProperties::class.java)
        ObjectifyService.register(SmoothedWeatherDataSet::class.java)
        ObjectifyService.register(WeatherDataSet::class.java)
        ObjectifyService.register(WeatherLocation::class.java)
        classesRegistered = true
    }
}

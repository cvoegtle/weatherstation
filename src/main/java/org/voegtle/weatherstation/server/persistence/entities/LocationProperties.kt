package org.voegtle.weatherstation.server.persistence.entities

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import org.voegtle.weatherstation.server.persistence.DataIndicies
import org.voegtle.weatherstation.server.util.DateUtil
import org.voegtle.weatherstation.server.util.StringUtil.isNotEmpty
import java.util.*

@Entity
class LocationProperties {
    @Id
    var id: Long? = null
    var location: String? = null
    var city: String? = null
    var cityShortcut: String? = null
    var timezone: String? = null
    var address: String? = null
    var secretHash: String? = null
    var readHash: String? = null
    var weatherForecast: String? = null
    var isWindRelevant: Boolean = false
    var windMultiplier: Float? = null
        get() {
            if (field == null) {
                field = 1.0f
            }
            return field
        }
    var indexOutsideTemperature: Int? = null
    var indexOutsideHumidity: Int? = null
    var indexInsideTemperature: Int? = null
    var indexInsideHumidity: Int? = null
    var expectedDataSets: Int? = null
    var expectedRequests: Int? = null
    var latitude: Float? = null
    var longitude: Float? = null
    val isValid: Boolean
        get() = (isNotEmpty(location)
                && isNotEmpty(city)
                && isNotEmpty(address))
    val dateUtil: DateUtil
        get() = DateUtil(TimeZone.getTimeZone(timezone))
    val dataIndices: DataIndicies
        get() = DataIndicies(indexOutsideTemperature, indexOutsideHumidity, indexInsideTemperature, indexInsideHumidity)
}

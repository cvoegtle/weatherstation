package org.voegtle.weatherstation.server

import com.googlecode.objectify.ObjectifyService
import com.googlecode.objectify.VoidWork
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletResponse
import org.json.JSONArray
import org.json.JSONObject
import org.voegtle.weatherstation.server.logic.caching.LocationPropertiesProvider
import org.voegtle.weatherstation.server.persistence.PersistenceManager
import org.voegtle.weatherstation.server.persistence.entities.Contact
import org.voegtle.weatherstation.server.persistence.entities.LocationProperties
import org.voegtle.weatherstation.server.persistence.entities.SmoothedWeatherDataSet
import org.voegtle.weatherstation.server.request.ResponseCode
import org.voegtle.weatherstation.server.request.ValidationException
import org.voegtle.weatherstation.server.util.HashService
import org.voegtle.weatherstation.server.util.JSONConverter
import registerClassesForPersistence
import java.io.IOException
import java.io.PrintWriter
import java.util.logging.Logger

abstract class AbstractServlet : HttpServlet() {
    protected val log = Logger.getLogger("ServletLogger")
    private val MIME_TYPE_APPLICATION_JSON = "application/json"

    protected val pm = PersistenceManager()
    internal var locationProperties: LocationProperties? = null
    internal var jsonConverter: JSONConverter? = null

    @Throws(ServletException::class)
    override fun init() {
        super.init()
        registerClassesForPersistence()

        runInObjectifyContext {
//                val lp = createLocationProperties()
//                pm.makePersistent(lp)
//
//                val contact = createContact()
//                pm.makePersistent(contact)

            locationProperties = LocationPropertiesProvider(pm).getLocationProperties()
            jsonConverter = JSONConverter(locationProperties!!)
        }

    }

    protected fun runInObjectifyContext(work: () -> Unit) {
        ObjectifyService.run(object : VoidWork() {
            override fun vrun() {
                work()
            }
        })
    }

    private fun createContact(): Contact {
        val contact = Contact()
        contact.mailAdress = "christian@voegtle.org"
        contact.name = "Christian Vögtle"
        contact.isReceiveDailyStatus = false
        contact.isReceiveIncidentReports = true
        return contact
    }

    private fun createLocationProperties(): LocationProperties {
        val lp = LocationProperties()
        lp.location = "wetterwolke"
        lp.address = "Entwicklungs Station"
        lp.cityShortcut = "DEV"
        lp.city = "Tegelweg 8"
        lp.weatherForecast = ""
        lp.secretHash = "2fe3974d34634baf28c732f4793724f11e4a0813a84030f962187b3844485ae4"
        lp.readHash = "a883d58dbbb62d60da3893c9822d19e43bc371d20ccc5bfdb341f2b120eea54c"
        lp.indexOutsideTemperature = 6
        lp.indexOutsideHumidity = 17
        lp.expectedDataSets = 500
        lp.expectedRequests = 500
        lp.timezone = "Europe/Berlin"

        return lp
    }

    internal fun returnDetailedResult(
        response: HttpServletResponse, list: List<SmoothedWeatherDataSet>,
        extended: Boolean
    ) {
        val jsonObjects = jsonConverter!!.toJson(list, extended)
        writeResponse(response, jsonObjects)
    }

    internal fun writeResponse(response: HttpServletResponse, jsonObject: JSONObject) {
        try {
            val out = response.writer
            response.contentType = MIME_TYPE_APPLICATION_JSON
            val responseString = jsonObject.toString()
            log.info(responseString)
            out.write(responseString)
            out.close()
        } catch (e: IOException) {
            log.severe("Could not write response.")
        }

    }

    @JvmOverloads
    internal fun writeResponse(
        response: HttpServletResponse, jsonObjects: List<JSONObject>,
        encoding: String = "UTF-8"
    ) {
        val jsonArray = JSONArray(jsonObjects)
        try {
            response.characterEncoding = encoding
            response.contentType = MIME_TYPE_APPLICATION_JSON
            val out = response.writer
            val responseString = jsonArray.toString()
            log.info(responseString)
            out.write(responseString)
            out.close()
        } catch (e: IOException) {
            log.severe("Could not write response.")
        }

    }

    internal fun returnResult(response: HttpServletResponse, result: String) {
        val out: PrintWriter
        try {
            out = response.writer
            response.contentType = "text/plain"
            out.println(result)
            out.close()
        } catch (e: IOException) {
            log.severe("Could not write response.")
        }

    }

    internal fun isCorrectLocation(location: String?): Boolean {
        return location == locationProperties!!.location
    }

    internal fun assertCorrectLocation(location: String?) {
        if (!isCorrectLocation(location)) {
            throw ValidationException(ResponseCode.WRONG_LOCATION)
        }
    }

    internal fun isSecretValid(secret: String?): Boolean {
        val secretHash = locationProperties!!.secretHash
        return secretHash == HashService.calculateHash(secret)
    }

    internal fun assertSecretValid(password: String?) {
        if (!isSecretValid(password)) {
            throw ValidationException(ResponseCode.NOT_AUTHORIZED)
        }
    }


    internal fun isReadSecretValid(secret: String?): Boolean {
        val readHash = locationProperties!!.readHash
        return isReadSecretValid(readHash, secret)
    }

    internal fun isReadSecretValid(readHash: String?, secret: String?): Boolean {
        return readHash == HashService.calculateHash(secret)
    }
}

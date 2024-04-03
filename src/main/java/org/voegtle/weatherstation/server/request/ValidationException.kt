package org.voegtle.weatherstation.server.request

class ValidationException(val responseCode: String) : Exception() {
}

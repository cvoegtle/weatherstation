package org.voegtle.weatherstation.server.request

object ResponseCode {
  val ACKNOWLEDGE = "ACK"
  val IGNORED = "IGNORED"
  val NOT_AUTHORIZED = "NOT AUTHORIZED"
  val PARSE_ERROR = "PARSE_ERROR"
  val WRONG_LOCATION = "WRONG LOCATION"
  val EMPTY = "EMPTY REQUEST"
}

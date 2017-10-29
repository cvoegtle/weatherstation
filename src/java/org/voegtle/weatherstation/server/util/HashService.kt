package org.voegtle.weatherstation.server.util

import java.security.MessageDigest
import java.util.logging.Logger

object HashService {
  private val log = Logger.getLogger(HashService::class.java.name)

  private val SALT = "AgentCoplien"
  private var md: MessageDigest? = null

  init {
    try {
      md = MessageDigest.getInstance("SHA-256")
    } catch (ex: Exception) {
      md = null
      log.severe("Instantiation of MessageDigest('SHA-256') failed")
    }

  }

  fun calculateHash(input: String): String {
    val hashedInput = md!!.digest((SALT + input).toByteArray(charset("UTF8")))
    return convertToHex(hashedInput);
  }

  private fun convertToHex(buffer: ByteArray): String {
    val sb = StringBuilder()
    buffer.forEach { aBuffer ->
      val value: Int = (aBuffer.toInt() and 0xff) + 0x100
      sb.append(value.toString(16).substring(1))
    }
    return sb.toString()
  }

  fun main(args: Array<String>) {
    for (str in args) {
      println(calculateHash(str))
    }
  }
}

package org.voegtle.weatherstation.server.util

import java.security.MessageDigest

object HashService {
  private val SALT = "AgentCoplien"
  private val md = MessageDigest.getInstance("SHA-256")

  fun calculateHash(input: String?): String {
    val hashedInput: ByteArray = md.digest(("$SALT${input ?: ""}").toByteArray(charset("UTF8")))

    return convertToHex(hashedInput)
  }

  private fun convertToHex(buffer: ByteArray): String {
    val sb = StringBuilder()
    for (aBuffer in buffer) {
      sb.append(Integer.toString((aBuffer.toInt() and 0xff) + 0x100, 16).substring(1))
    }
    return sb.toString()
  }

}

/*fun main(args: Array<String>) {
  for (str in args) {
    println(HashService.calculateHash(str))
  }
} */

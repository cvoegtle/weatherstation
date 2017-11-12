package org.voegtle.weatherstation.server.parser

import java.util.ArrayList
import java.util.StringTokenizer

class DataLine(data: String) {
  private val values = ArrayList<String>()

  init {
    var data = data
    data = data.replace(" ", "+")
    data = data.replace(";;", "; ;")
    data = data.replace(";;", "; ;")
    val tokenizer = StringTokenizer(data, ";\n")
    while (tokenizer.hasMoreTokens()) {
      values.add(tokenizer.nextToken().trim { it <= ' ' })
    }
  }

  operator fun get(index: Int): String? {
    return if (index >= values.size) {
      null
    } else values[index]
  }

  fun size() = values.size

  override fun toString(): String {
    val sb = StringBuilder()
    var i = 0
    for (value in values) {
      sb.append(i++)
      sb.append("=<")
      sb.append(value)
      sb.append("> ")
    }
    return sb.toString()
  }
}

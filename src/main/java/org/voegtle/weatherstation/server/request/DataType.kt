package org.voegtle.weatherstation.server.request

enum class DataType private constructor(private val id: String) {
  AGGREGATED("aggregated"), DETAIL("detail"), CURRENT("current"), ALL("all"), RAIN("rain"), STATS("stats"), UNDEFINED("undefined");

  override fun toString(): String {
    return id
  }

  companion object {

    fun fromString(id: String): DataType {
      return DataType.values().firstOrNull { it.id == id }
          ?: UNDEFINED
    }
  }
}

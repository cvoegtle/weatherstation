package org.voegtle.weatherstation.server.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class TestHashService {
  @Test fun checkHashNull() {
    val hashNull = HashService.calculateHash(null)
    val hashEmpty = HashService.calculateHash(null)
    assertEquals(hashEmpty, hashNull)
  }
}
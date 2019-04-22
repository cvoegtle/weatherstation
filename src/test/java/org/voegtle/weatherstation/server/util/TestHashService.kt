package org.voegtle.weatherstation.server.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TestHashService {
  @Test fun checkHashNull() {
    val hashNull = HashService.calculateHash(null)
    val hashEmpty = HashService.calculateHash(null)
    assertEquals(hashEmpty, hashNull)
  }
}
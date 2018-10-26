package org.voegtle.weatherstation.server.logic

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CalculationRuleTest {
  @Test fun checkOverflowCorrection() {
    assertEquals(4027, makeOverflowCorrection(4027, 4029))
  }
}
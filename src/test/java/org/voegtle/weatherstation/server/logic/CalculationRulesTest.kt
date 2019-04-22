package org.voegtle.weatherstation.server.logic

import org.junit.Assert.assertEquals
import org.junit.Test


class CalculationRuleTest {
  @Test fun checkOverflowCorrection() {
    assertEquals(4027, makeOverflowCorrection(4027, 4029))
  }

  @Test fun checkEqualValues() {
    assertEquals(4037, makeOverflowCorrection(4037, 4037))
  }
  @Test fun checkEqualValuesBelowThreshold() {
    assertEquals(3037, makeOverflowCorrection(3037, 3037))
  }
  @Test fun checkValuesBelowThreshold() {
    assertEquals(3037, makeOverflowCorrection(3037, 3047))
  }
}
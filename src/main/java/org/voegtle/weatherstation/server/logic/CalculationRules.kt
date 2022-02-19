package org.voegtle.weatherstation.server.logic

private val OVERFLOW = 4096
private val CORRECTION_THRESHOLD = 4000
fun makeOverflowCorrection(olderCount: Int, youngerCount: Int): Int =
    if (olderCount > youngerCount && olderCount > CORRECTION_THRESHOLD && youngerCount > 0) olderCount - OVERFLOW else olderCount


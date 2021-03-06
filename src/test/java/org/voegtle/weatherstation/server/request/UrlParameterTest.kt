package org.voegtle.weatherstation.server.request

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.servlet.http.HttpServletRequest

class UrlParameterTest {
  @Test fun checkCentralUrlParameter() {
    val request = Mockito.mock(HttpServletRequest::class.java)
    Mockito.`when`(request.getParameter("build")).thenReturn("544")
    Mockito.`when`(request.getParameter("locations")).thenReturn("tegelweg8,leoxity,bali,herzo,elb")
    val parameter = CentralUrlParameter(request)
    Assertions.assertEquals(true, parameter.isUtf8)
    Assertions.assertEquals("544", parameter.buildNumber)
  }
}
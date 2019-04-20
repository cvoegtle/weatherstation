package org.voegtle.weatherstation.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
  println("starte Wetterwolke2")
  SpringApplication.run(Application::class.java, *args)
}

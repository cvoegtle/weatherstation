package org.voegtle.weatherstation.server.image

import org.voegtle.weatherstation.server.util.StringUtil

import java.util.HashMap

class ImageServers {
  private val servers = HashMap<Int, String>()

  init {
    servers.put(0, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc")
    servers.put(1, "https://docs.google.com/spreadsheets/d/1ahkm9SDTqjYcsLgKIH5yjmqlAh6dKxgfIrZA5Dt9L3o/pubchart")
    servers.put(2, "https://docs.google.com/spreadsheets/d/1eWZ9CoA004HtqDuZWaoeUrhytwkmzCZ9pRlxxw_2gO8/pubchart")
  }

  operator fun get(id: Int?): String {
    var server = servers[id]
    if (StringUtil.isEmpty(server)) {
      server = servers[0]
    }
    return server!!
  }
}

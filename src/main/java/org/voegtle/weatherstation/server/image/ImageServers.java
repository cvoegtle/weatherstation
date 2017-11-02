package org.voegtle.weatherstation.server.image;

import org.voegtle.weatherstation.server.util.StringUtil;

import java.util.HashMap;

public class ImageServers {
  private HashMap<Integer, String> servers = new HashMap<>();

  public ImageServers() {
    servers.put(0, "https://docs.google.com/spreadsheet/oimg?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc");
    servers.put(1, "https://docs.google.com/spreadsheets/d/1ahkm9SDTqjYcsLgKIH5yjmqlAh6dKxgfIrZA5Dt9L3o/pubchart");
    servers.put(2, "https://docs.google.com/spreadsheets/d/1eWZ9CoA004HtqDuZWaoeUrhytwkmzCZ9pRlxxw_2gO8/pubchart");
  }

  public String get(Integer id) {
    String server = servers.get(id);
    if (StringUtil.isEmpty(server)) {
      server = servers.get(0);
    }
    return server;
  }
}

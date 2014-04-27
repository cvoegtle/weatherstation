package org.voegtle.weatherstation.client.dto;

import java.io.Serializable;

public class LoginDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private String url;
  private String description;

  public LoginDTO() {
  }

  public LoginDTO(String url, String description) {
    this.description = description;
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getDescription() {
    return description;
  }

}

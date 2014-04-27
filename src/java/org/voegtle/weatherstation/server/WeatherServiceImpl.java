package org.voegtle.weatherstation.server;

import org.voegtle.weatherstation.client.WeatherService;
import org.voegtle.weatherstation.client.dto.LoginDTO;
import org.voegtle.weatherstation.client.dto.WeatherDTO;
import org.voegtle.weatherstation.server.logic.WeatherDataFetcher;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WeatherServiceImpl extends RemoteServiceServlet implements WeatherService {
  private static final long serialVersionUID = 1L;
  private PersistenceManager pm = new PersistenceManager();

  @Override
  public LoginDTO login(String requestUri) {
    UserService us = UserServiceFactory.getUserService();
    User user = us.getCurrentUser();

    if (user != null) {
      return new LoginDTO(us.createLogoutURL(requestUri), "logout " + user.getEmail());
    } else {
      return new LoginDTO(us.createLoginURL(requestUri), "login");
    }
  }

  @Override
  public WeatherDTO getLatestWeatherData() {
    return new WeatherDataFetcher(pm).getLatestWeatherData();
  }

}

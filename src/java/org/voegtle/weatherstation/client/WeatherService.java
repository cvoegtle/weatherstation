package org.voegtle.weatherstation.client;

import org.voegtle.weatherstation.client.dto.LoginDTO;
import org.voegtle.weatherstation.client.dto.WeatherDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service")
public interface WeatherService extends RemoteService {
  LoginDTO login(String requestUri);

  WeatherDTO getLatestWeatherData();
}

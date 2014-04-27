package org.voegtle.weatherstation.client;

import org.voegtle.weatherstation.client.dto.LoginDTO;
import org.voegtle.weatherstation.client.dto.WeatherDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WeatherServiceAsync {

  void getLatestWeatherData(AsyncCallback<WeatherDTO> callback);

  void login(String requestUri, AsyncCallback<LoginDTO> callback);

}

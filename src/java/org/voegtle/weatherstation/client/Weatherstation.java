package org.voegtle.weatherstation.client;

import org.voegtle.weatherstation.client.dto.LoginDTO;
import org.voegtle.weatherstation.client.dto.WeatherDTO;
import org.voegtle.weatherstation.client.util.StringUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Weatherstation implements EntryPoint {
  private final WeatherServiceAsync weatherService = GWT.create(WeatherService.class);
  private static int UPDATE_INTERVALL = 155;

  private Label infoLabel = new Label();

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    RootPanel.get("problem").add(infoLabel);

    if (StringUtil.isNotEmpty(Window.Location.getParameter("login"))) {
      loginBeforeFetchWeatherData();
    } else {
      startWeatherDataTimer();
    }
  }

  private void loginBeforeFetchWeatherData() {
    weatherService.login(Window.Location.getHref(), new AsyncCallback<LoginDTO>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Login fehlgeschlagen.\n" + caught.getCause());
      }

      @Override
      public void onSuccess(LoginDTO loginDTO) {
        if (RootPanel.get("login") != null) {
          RootPanel.get("login").add(new Anchor(loginDTO.getDescription(), loginDTO.getUrl()));
        }
        startWeatherDataTimer();
      }
    });
  }

  private void fetchWeatherData() {
    weatherService.getLatestWeatherData(new AsyncCallback<WeatherDTO>() {
      public void onFailure(Throwable caught) {
        infoLabel.setText("(Verbindung zum Server gestört)");
      }

      public void onSuccess(WeatherDTO result) {
        clear();
        RootPanel.get("time").add(new Label(result.getTime()));
        RootPanel.get("temperature").add(new Label(result.getTemperature()));
        RootPanel.get("insideTemp").add(new Label(result.getInsideTemperature()));
        RootPanel.get("humidity").add(new Label(result.getHumidity()));
        RootPanel.get("insideHumidity").add(new Label(result.getInsideHumidity()));

        if (result.getRainLastHour() != null) {
          RootPanel.get("rainLabel").add(new Label("Niederschlag 1h:"));
          RootPanel.get("rain").add(new Label(result.getRainLastHour()));
        } else if (result.getRaining() != null && result.getRaining() == true) {
          RootPanel.get("rainLabel").add(new Label("es regnet"));
        }
        if (RootPanel.get("wind") != null) {
          RootPanel.get("wind").add(new Label(result.getWindspeed()));
        }

      }
    });
  }

  private void clear() {
    RootPanel.get("time").clear();
    RootPanel.get("temperature").clear();
    RootPanel.get("insideTemp").clear();
    RootPanel.get("humidity").clear();
    RootPanel.get("insideHumidity").clear();
    RootPanel.get("rain").clear();
    RootPanel.get("rainLabel").clear();
    if (RootPanel.get("wind") != null) {
      RootPanel.get("wind").clear();
    }
    infoLabel.setText("");
  }

  private void startWeatherDataTimer() {
    fetchWeatherData();

    Timer timer = new Timer() {

      @Override
      public void run() {
        fetchWeatherData();
      }
    };
    timer.scheduleRepeating(UPDATE_INTERVALL * 1000);
  }
}

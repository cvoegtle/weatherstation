package org.voegtle.weatherstation.server.logic;

import org.json.JSONObject;
import org.voegtle.weatherstation.server.data.UnformattedWeatherDTO;
import org.voegtle.weatherstation.server.persistence.LocationProperties;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.util.JSONConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

class WeatherDataForwarder {
  private static final Logger log = Logger.getLogger(WeatherDataForwarder.class.getName());
  private static final int COMMUNICATION_TIMEOUT = 60000;
  private WeatherDataFetcher weatherDataFetcher;
  private JSONConverter jsonConverter;

  WeatherDataForwarder(PersistenceManager pm, LocationProperties locationProperties) {
    weatherDataFetcher = new WeatherDataFetcher(pm, locationProperties);
    jsonConverter = new JSONConverter(locationProperties);
  }

  void forwardLastDataset() {
    UnformattedWeatherDTO latest = weatherDataFetcher.getLatestWeatherDataUnformatted(true);
    forward(jsonConverter.toJson(latest));
  }

  private void forward(JSONObject json) {
    try {
      byte encodedBytes[] = json.toString().getBytes("UTF-8");

      URL wetterCentral = new URL("https://wettercentral.appspot.com/weatherstation/cache");
      HttpURLConnection wetterConnection = (HttpURLConnection) wetterCentral.openConnection();
      wetterConnection.setConnectTimeout(COMMUNICATION_TIMEOUT);
      wetterConnection.setReadTimeout(COMMUNICATION_TIMEOUT);
      wetterConnection.setRequestMethod("POST");
      wetterConnection.setDoOutput(true);

      try {
        wetterConnection.getOutputStream().write(encodedBytes);
        wetterConnection.getOutputStream().close();
        log.info("forwarded <" + json.toString() + ">");
        String response = read(wetterConnection.getInputStream());
      } finally {
        wetterConnection.getInputStream().close();
        wetterConnection.disconnect();
      }
      
    } catch (IOException e) {
      log.warning("failed forwarding to wettercentral. " + e.getMessage());
    }
  }

  private String read(InputStream inputStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    String line = reader.readLine();
    return line;
  }

  private WeatherDataSet getLast(List<WeatherDataSet> dataSets) {
    int lastIndex = dataSets.size() - 1;
    return dataSets.get(lastIndex);
  }
}

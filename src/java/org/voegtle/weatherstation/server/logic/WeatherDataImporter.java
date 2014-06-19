package org.voegtle.weatherstation.server.logic;

import org.voegtle.weatherstation.server.parser.DataLine;
import org.voegtle.weatherstation.server.parser.DataParser;
import org.voegtle.weatherstation.server.persistence.PersistenceManager;
import org.voegtle.weatherstation.server.persistence.WeatherDataSet;
import org.voegtle.weatherstation.server.request.ResponseCode;

import java.text.ParseException;
import java.util.ArrayList;

public class WeatherDataImporter {
  private PersistenceManager pm;

  public WeatherDataImporter(PersistenceManager pm) {
    this.pm = pm;
  }

  public String doImport(DataLine line) {
    ArrayList<DataLine> lines = new ArrayList<>();
    lines.add(line);
    return doImport(lines);
  }

  public String doImport(ArrayList<DataLine> lines) {
    String result;
    try {
      boolean persisted = false;
      DataParser parser = new DataParser();
      for (DataLine dataLine : lines) {
        WeatherDataSet dataSet = parser.parse(dataLine);
        if (pm.makePersitant(dataSet)) {
          persisted = true;
        }
      }

      if (persisted) {
        new WeatherDataSmoother(pm).smoothWeatherData();
      }
      result = persisted ? ResponseCode.ACKNOWLEDGE : ResponseCode.IGNORED;
    } catch (ParseException ex) {
      result = ResponseCode.PARSE_ERROR;
    }
    return result;
  }


}

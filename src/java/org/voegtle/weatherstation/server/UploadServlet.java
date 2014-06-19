package org.voegtle.weatherstation.server;

import org.voegtle.weatherstation.server.logic.WeatherDataImporter;
import org.voegtle.weatherstation.server.parser.DataLine;
import org.voegtle.weatherstation.server.request.IncomingUrlParameter;
import org.voegtle.weatherstation.server.request.ResponseCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class UploadServlet extends AbstractServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result;

    IncomingUrlParameter param = new IncomingUrlParameter(request);
    if (isSecretValid(param.getSecret())) {
      ArrayList<DataLine> lines = readInputLines(request.getInputStream());
      WeatherDataImporter importer = new WeatherDataImporter(pm);
      result = importer.doImport(lines);
    } else {
      result = ResponseCode.NOT_AUTHORIZED;
    }

    returnResult(response, result);
  }

  private ArrayList<DataLine> readInputLines(InputStream inputStream) {
    ArrayList<DataLine> lines = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(new DataLine(line));
      }
    } catch (IOException ex) {
      log.severe("failed to read POST input stream");
    }
    return lines;
  }
}

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
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;

public class UploadServlet extends AbstractServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String result;

    IncomingUrlParameter param = new IncomingUrlParameter(request);
    if (isSecretValid(param.getSecret())) {
      if (isCorrectLocation(param.getLocation())) {
        ArrayList<DataLine> lines = readInputLines(getContentStream(request));
        WeatherDataImporter importer = new WeatherDataImporter(pm);
        result = importer.doImport(lines);
      } else {
        result = ResponseCode.WRONG_LOCATION;
      }
    } else {
      log.warning("request without authorisation. Sent secret: " + param.getSecret());
      result = ResponseCode.NOT_AUTHORIZED;
    }

    returnResult(response, result);
  }

  private BufferedReader getContentStream(HttpServletRequest request) throws IOException {
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String val = (String) parameterNames.nextElement();
      if (val.startsWith("$1")) {
        return new BufferedReader(new StringReader(val));
      }
    }
    return new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

  }

  private ArrayList<DataLine> readInputLines(BufferedReader reader) {
    ArrayList<DataLine> lines = new ArrayList<>();
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(new DataLine(line));
      }
      log.info("number of datalines: " + lines.size());
      for (DataLine dl : lines) {
        log.info(dl.toString());
      }
    } catch (IOException ex) {
      log.severe("failed to read POST input stream");
    }
    return lines;
  }

}

package org.voegtle.weatherstation.server;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Enumeration;

public abstract class AbstractInputServlet extends AbstractServlet {

  protected String readString(HttpServletRequest request) throws IOException {
    StringBuffer buffer = new StringBuffer();
    BufferedReader reader = getContentStream(request);
    String line;
    while ((line = reader.readLine()) != null) {
      buffer.append(line);
    }
    return buffer.toString();
  }

  protected BufferedReader getContentStream(HttpServletRequest request) throws IOException {
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String val = (String) parameterNames.nextElement();
      if (val.startsWith("$1")) {
        return new BufferedReader(new StringReader(val));
      }
    }
    return new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
  }

}

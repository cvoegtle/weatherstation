package org.voegtle.weatherstation.server;

import org.voegtle.weatherstation.server.logic.AdminNotifier;
import org.voegtle.weatherstation.server.request.ResponseCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReportingServlet extends AbstractServlet {

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    AdminNotifier notifier = new AdminNotifier(pm, locationProperties);
    notifier.notifiy();
    returnResult(response, ResponseCode.ACKNOWLEDGE);
  }
}

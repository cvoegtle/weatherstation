package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

abstract class AbstractUrlParameter {
  protected static final String FORMAT_TIMESTAMP = "yyyy-MM-dd-HH:mm:ss";

  final private HttpServletRequest request;

  protected AbstractUrlParameter(final HttpServletRequest request) {
    this.request = request;
  }

  protected String getUrlParameter(String paramName) {
    String param = request.getParameter(paramName);
    if ("".equals(param)) {
      return null;
    }
    return param;
  }

  protected boolean getUrlParameterBoolean(String paramName) {
    String param = request.getParameter(paramName);
    return param != null;
  }

  protected Date getUrlParameterDate(String paramName) {
    String param = request.getParameter(paramName);
    if ("".equals(param) || param == null) {
      return null;
    }

    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIMESTAMP);
    Date result = null;
    try {
      result = sdf.parse(param);
      result = DateUtil.fromCESTtoGMT(result);
    } catch (ParseException e) {
    }

    return result;
  }

  protected DataType getUrlParameterType(String paramName) {
    String param = request.getParameter(paramName);
    return DataType.fromString(param);
  }
}

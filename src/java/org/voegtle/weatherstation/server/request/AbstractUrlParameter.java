package org.voegtle.weatherstation.server.request;

import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

abstract class AbstractUrlParameter {
  private static final String FORMAT_TIMESTAMP = "yyyy-MM-dd-HH:mm:ss";

  final private HttpServletRequest request;
  private final DateUtil dateUtil;

  protected AbstractUrlParameter(final HttpServletRequest request, DateUtil dateUtil) {
    this.request = request;
    this.dateUtil = dateUtil;
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

  protected Date getUrlParameterDate(String paramName, boolean localTimezone) {
    String param = request.getParameter(paramName);
    if (StringUtil.isEmpty(param)) {
      return null;
    }

    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIMESTAMP);
    Date result;
    try {
      result = sdf.parse(param);
      if (localTimezone) {
        result = dateUtil.fromLocalToGMT(result);
        result = dateUtil.fromGMTtoCEST(result);
      } else {
        result = dateUtil.fromCESTtoGMT(result);
      }
    } catch (ParseException e) {
      result = null;
    }

    return result;
  }

  protected Integer getUrlParameterInteger(String paramName) {
    return getUrlParameterInteger(paramName, null);
  }

  protected Integer getUrlParameterInteger(String paramName, Integer defaultValue) {
    String param = request.getParameter(paramName);
    if (StringUtil.isNotEmpty(param)) {
      return new Integer(param);
    }
    return defaultValue;
  }

  protected DataType getUrlParameterType(String paramName, DataType defaultType) {
    String param = request.getParameter(paramName);
    DataType dataType = DataType.fromString(param);
    if (dataType.equals(DataType.UNDEFINED)) {
      dataType = defaultType;
    }
    return dataType;
  }

  protected DataType getUrlParameterType(String paramName) {
    String param = request.getParameter(paramName);
    return DataType.fromString(param);
  }
}

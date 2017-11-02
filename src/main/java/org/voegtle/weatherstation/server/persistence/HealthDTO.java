package org.voegtle.weatherstation.server.persistence;

import java.io.Serializable;
import java.util.Date;

public class HealthDTO implements Serializable {
  private Date day;

  private int requests;
  private int lines;
  private int persisted;

  public HealthDTO() {
  }

  public HealthDTO(Date day) {
    this.day = day;
  }

  public void incrementRequests() {
    this.requests++;
  }

  public void incrementLines(int newLines) {
    this.lines += newLines;
  }

  public void incrementPersisted(int persisted) {
    this.persisted += persisted;
  }

  public Date getDay() {
    return day;
  }

  public void setDay(Date day) {
    this.day = day;
  }

  public int getRequests() {
    return requests;
  }

  public void setRequests(int requests) {
    this.requests = requests;
  }

  public int getLines() {
    return lines;
  }

  public void setLines(int validDatasets) {
    this.lines = validDatasets;
  }

  public int getPersisted() {
    return persisted;
  }

  public void setPersisted(int invalidDatasets) {
    this.persisted = invalidDatasets;
  }
}

package org.voegtle.weatherstation.server.persistence.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Contact {
  @Id
  Long id;

  private String mailAdress;

  private String name;
  private boolean receiveDailyStatus;
  private boolean receiveIncidentReports;

  public String getMailAdress() {
    return mailAdress;
  }

  public void setMailAdress(String mailAdress) {
    this.mailAdress = mailAdress;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isReceiveDailyStatus() {
    return receiveDailyStatus;
  }

  public void setReceiveDailyStatus(boolean receiveDailyStatus) {
    this.receiveDailyStatus = receiveDailyStatus;
  }

  public boolean isReceiveIncidentReports() {
    return receiveIncidentReports;
  }

  public void setReceiveIncidentReports(boolean receiveIncidentReports) {
    this.receiveIncidentReports = receiveIncidentReports;
  }
}

package org.voegtle.weatherstation.server.persistence;

import com.google.appengine.api.datastore.Key;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Health {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private Date day;

  private int requests;
  private int lines;
  private int persisted;

  public Health() {
  }

  public Health(Date day) {
    this.day = day;
  }

  public Health(HealthDTO dto) {
    fromDTO(dto);
  }

  public void fromDTO(HealthDTO dto) {
    this.day = dto.getDay();
    this.requests = dto.getRequests();
    this.lines = dto.getLines();
    this.persisted = dto.getPersisted();
  }

  public HealthDTO toDTO() {
    HealthDTO dto = new HealthDTO(this.getDay());
    dto.setRequests(this.getRequests());
    dto.setLines(this.getLines());
    dto.setPersisted(this.getPersisted());
    return dto;
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
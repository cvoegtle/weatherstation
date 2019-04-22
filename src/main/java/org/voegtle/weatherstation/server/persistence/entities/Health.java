package org.voegtle.weatherstation.server.persistence.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import org.voegtle.weatherstation.server.persistence.HealthDTO;

import java.util.Date;

@Entity
public class Health {
  @Id
  Long id;

  @Index private Date day;

  private int requests;
  private int lines;
  private int persisted;

  public Health() {
  }

  public Health(Date day) {
    this.day = day;
  }

  public void fromDTO(HealthDTO dto) {
    this.day = dto.getDay();
    this.requests = dto.getRequests();
    this.lines = dto.getLines();
    this.persisted = dto.getPersisted();
  }

  public HealthDTO toDTO() {
    Date day = new Date(this.getDay().getTime()); // convert from datanucleus date to java.util.Date
    return new HealthDTO(day, this.getRequests(), this.getLines(), this.getPersisted());
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

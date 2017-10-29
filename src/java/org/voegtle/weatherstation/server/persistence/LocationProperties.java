package org.voegtle.weatherstation.server.persistence;

import com.google.appengine.api.datastore.Key;
import org.voegtle.weatherstation.server.util.DateUtil;
import org.voegtle.weatherstation.server.util.StringUtil;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.TimeZone;

@Entity
public class LocationProperties {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  private String location;
  private String city;
  private String cityShortcut;
  private String timezone;

  private String address;

  private String secretHash;

  private String readHash;

  private String weatherForecast;

  private Boolean windRelevant;

  private Float windMultiplier;

  private Integer indexOutsideTemperature;
  private Integer indexOutsideHumidity;

  private Integer indexInsideTemperature;
  private Integer indexInsideHumidity;

  private Integer expectedDataSets;
  private Integer expectedRequests;

  private Float latitude;
  private Float longitude;

  public LocationProperties() {
  }

  public boolean isValid() {
    return StringUtil.INSTANCE.isNotEmpty(location) && StringUtil.INSTANCE.isNotEmpty(city) && StringUtil.INSTANCE.isNotEmpty(address);
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String id) {
    this.location = id;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCityShortcut() {
    return cityShortcut;
  }

  public void setCityShortcut(String cityShortcut) {
    this.cityShortcut = cityShortcut;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSecretHash() {
    return secretHash;
  }

  public void setSecretHash(String secretHash) {
    this.secretHash = secretHash;
  }

  public String getWeatherForecast() {
    return weatherForecast;
  }

  public void setWeatherForecast(String weatherForecast) {
    this.weatherForecast = weatherForecast;
  }


  public String getReadHash() {
    return readHash;
  }

  public void setReadHash(String readHash) {
    this.readHash = readHash;
  }

  public boolean isWindRelevant() {
    return windRelevant != null && windRelevant;
  }

  public void setWindRelevant(Boolean windRelevant) {
    this.windRelevant = windRelevant;
  }

  public Float getWindMultiplier() {
    if (windMultiplier == null) {
      windMultiplier = 1.0f;
    }
    return windMultiplier;
  }

  public void setWindMultiplier(Float windMultiplier) {
    this.windMultiplier = windMultiplier;
  }

  public String getTimezone() {
    return timezone;
  }

  public DateUtil getDateUtil() {
    return new DateUtil(TimeZone.getTimeZone(timezone));
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public Integer getIndexOutsideTemperature() {
    return indexOutsideTemperature;
  }

  public void setIndexOutsideTemperature(Integer indexOutsideTemperature) {
    this.indexOutsideTemperature = indexOutsideTemperature;
  }

  public Integer getIndexOutsideHumidity() {
    return indexOutsideHumidity;
  }

  public void setIndexOutsideHumidity(Integer indexOutsideHumidity) {
    this.indexOutsideHumidity = indexOutsideHumidity;
  }

  public Integer getIndexInsideTemperature() {
    return indexInsideTemperature;
  }

  public void setIndexInsideTemperature(Integer indexInsideTemperature) {
    this.indexInsideTemperature = indexInsideTemperature;
  }

  public Integer getIndexInsideHumidity() {
    return indexInsideHumidity;
  }

  public void setIndexInsideHumidity(Integer indexInsideHumidity) {
    this.indexInsideHumidity = indexInsideHumidity;
  }

  public DataIndicies getDataIndices() {
    DataIndicies di = new DataIndicies();

    di.indexInsideHumidity = indexInsideHumidity;
    di.indexInsideTemperature = indexInsideTemperature;
    di.indexOutsideHumidity = indexOutsideHumidity;
    di.indexOutsideTemperature = indexOutsideTemperature;

    return di;
  }

  public Integer getExpectedDataSets() {
    return expectedDataSets;
  }

  public void setExpectedDataSets(Integer expectedDataSet) {
    this.expectedDataSets = expectedDataSet;
  }

  public Integer getExpectedRequests() {
    return expectedRequests;
  }

  public void setExpectedRequests(Integer expectedRequests) {
    this.expectedRequests = expectedRequests;
  }

  public Float getLatitude() {
    return latitude;
  }

  public void setLatitude(Float latitude) {
    this.latitude = latitude;
  }

  public Float getLongitude() {
    return longitude;
  }

  public void setLongitude(Float longitude) {
    this.longitude = longitude;
  }
}

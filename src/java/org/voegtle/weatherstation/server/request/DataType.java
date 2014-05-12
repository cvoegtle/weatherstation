package org.voegtle.weatherstation.server.request;

public enum DataType {
  AGGREGATED("aggregated"), DETAIL("detail"), CURRENT("current"), ALL("all"), RAIN("rain"), UNDEFINED("undefined");

  String name;

  private DataType(String name) {
    this.name = name;
  }

  public static DataType fromString(String name) {
    for (DataType type : DataType.values()) {
      if (type.name.equals(name)) {
        return type;
      }
    }
    return UNDEFINED;
  }

  @Override
  public String toString() {
    return name;
  }
}

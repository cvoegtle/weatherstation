package org.voegtle.weatherstation.server.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DataLine {
  private final ArrayList<String> values = new ArrayList<>();

  public DataLine(String data) {
    data = data.replace(" ", "+");
    data = data.replace(";;", "; ;");
    data = data.replace(";;", "; ;");
    StringTokenizer tokenizer = new StringTokenizer(data, ";\n");
    while (tokenizer.hasMoreTokens()) {
      values.add(tokenizer.nextToken().trim());
    }
  }

  public String get(int index) {
    if (index >= values.size()) {
      return null;
    }
    return values.get(index);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (String val : values) {
      sb.append(i++);
      sb.append("=<");
      sb.append(val);
      sb.append("> ");
    }
    return sb.toString();
  }
}

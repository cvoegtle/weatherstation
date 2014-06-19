package org.voegtle.weatherstation.server.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DataLine {
  ArrayList<String> values = new ArrayList<>();

  public DataLine(String data) {
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
}

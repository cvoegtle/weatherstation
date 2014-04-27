package org.voegtle.weatherstation.server.util;

import java.security.MessageDigest;

public class HashService {
  private static String SALT = "AgentCoplien";
  private static MessageDigest md;

  static {
    try {
    md = MessageDigest.getInstance("SHA-256");
    } catch (Exception ex) {}
  }

  public static String calculateHash(String input) {
    byte[] hashedInput = null;
    try {
       hashedInput = md.digest((SALT + input).getBytes("UTF8"));
    } catch (Exception ex) {}

    return convertToHex(hashedInput);
  }

  private static String convertToHex(byte[] buffer) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < buffer.length; i++) {
      sb.append(Integer.toString((buffer[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    for (String str : args) {
      System.out.println(calculateHash(str));
    }
  }
}

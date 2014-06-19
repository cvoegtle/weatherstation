package org.voegtle.weatherstation.server.util;

import java.security.MessageDigest;
import java.util.logging.Logger;

public class HashService {
  protected static final Logger log = Logger.getLogger(HashService.class.getName());

  private static String SALT = "AgentCoplien";
  private static MessageDigest md;

  static {
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (Exception ex) {
      log.severe("Instantiation of MessageDigest('SHA-256') failed");
    }
  }

  public static String calculateHash(String input) {
    byte[] hashedInput;
    try {
      hashedInput = md.digest((SALT + input).getBytes("UTF8"));
    } catch (Exception ex) {
      hashedInput = null;
    }

    return convertToHex(hashedInput);
  }

  private static String convertToHex(byte[] buffer) {
    StringBuilder sb = new StringBuilder();
    for (byte aBuffer : buffer) {
      sb.append(Integer.toString((aBuffer & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    for (String str : args) {
      System.out.println(calculateHash(str));
    }
  }
}

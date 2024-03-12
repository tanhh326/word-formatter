package com.crane.wordformat.formatter.utils;

public class MatchUtils {

  public static boolean match(String text, String keyword) {
    String str = "^\\s*";
    for (char c : keyword.toCharArray()) {
      str += c + "\\s*";
    }
    return text.matches(str + "\n$");
  }
}

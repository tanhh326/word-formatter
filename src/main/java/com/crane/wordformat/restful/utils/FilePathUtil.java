package com.crane.wordformat.restful.utils;

public class FilePathUtil {

  public static String build(String ext, String... args) {
    return String.join("/", args) + "." + ext;
  }
}

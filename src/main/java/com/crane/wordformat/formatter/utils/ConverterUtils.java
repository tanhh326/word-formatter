package com.crane.wordformat.formatter.utils;

import java.util.Stack;

public class ConverterUtils {

  public static int zhToNum(String string) {
    Stack<Integer> stack = new Stack<>();
    String numStr = "一二三四五六七八九";
    String unitStr = "十百千万亿";
    String[] ssArr = string.split("");
    for (String e : ssArr) {
      int numIndex = numStr.indexOf(e);
      int unitIndex = unitStr.indexOf(e);
      if (numIndex != -1) {
        stack.push(numIndex + 1);
      } else if (unitIndex != -1) {
        int unitNum = (int) Math.pow(10, unitIndex + 1);
        if (stack.isEmpty()) {
          stack.push(unitNum);
        } else {
          stack.push(stack.pop() * unitNum);
        }
      }
    }
    return stack.stream().mapToInt(s -> s).sum();
  }

  public static String removeEmpty(String text) {
    return text.replaceAll("[\\p{Z}\\s+]", "");
  }

  public static String keywordToRegex(String text) {
    String str = "^\\s*";
    for (char c : text.toCharArray()) {
      str += c + "\\s*";
    }
    return str + "\n$";
  }
}

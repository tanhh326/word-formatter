package com.crane.wordformat.formatter.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;


/**
 * 内容中英文分组
 */
@Data
public class TextLanguageGroupDto {

  // 匹配到的所有字符
  private final List<Character> chars = new ArrayList<>();

  private boolean isZh;

  public String getText() {
    return chars.stream().map(String::valueOf).reduce((a, b) -> a + b).orElse("");
  }
}

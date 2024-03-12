package com.crane.wordformat.formatter.enums;

import com.aspose.words.LineSpacingRule;

/**
 * 行间距规则枚举
 */
public enum LineSpacingRuleEnum {
  AT_LEAST(LineSpacingRule.AT_LEAST),

  EXACT(LineSpacingRule.EXACTLY),


  MULTIPLE(LineSpacingRule.MULTIPLE);

  private final int value;

  LineSpacingRuleEnum(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}

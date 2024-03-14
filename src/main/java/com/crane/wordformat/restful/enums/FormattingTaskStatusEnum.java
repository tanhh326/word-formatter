package com.crane.wordformat.restful.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum FormattingTaskStatusEnum implements IEnum<Integer> {
  SUCCESS(1), FAIL(0), PROCESSING(2);

  private final int value;

  FormattingTaskStatusEnum(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }
}

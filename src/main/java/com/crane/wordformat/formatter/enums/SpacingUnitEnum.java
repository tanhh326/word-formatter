package com.crane.wordformat.formatter.enums;

import com.aspose.words.ConvertUtil;
import java.util.function.Function;

/**
 * 间距单位枚举
 */
public enum SpacingUnitEnum {
  POINT(value -> value),
  CM(value -> ConvertUtil.millimeterToPoint(value * 10.0)),
  MM(value -> ConvertUtil.millimeterToPoint(value)),
  INCH(value -> ConvertUtil.inchToPoint(value)),
  LINE(value -> value),
  AUTO(value -> value),
  DOUBLE(value -> value * 12);

  /**
   * 单位转换器
   */
  public final Function<Double, Double> converter;

  SpacingUnitEnum(Function<Double, Double> converter) {
    this.converter = converter;
  }
}

package com.crane.wordformat.formatter.enums;

import com.aspose.words.ConvertUtil;
import java.util.function.Function;

/**
 * 缩进单位枚举
 */
public enum IndentUnitEnum {
  POINT(value -> value),
  CM(value -> ConvertUtil.millimeterToPoint(value * 10.0)),
  MM(value -> ConvertUtil.millimeterToPoint(value)),
  INCH(value -> ConvertUtil.inchToPoint(value)),
  CHARACTER(value -> value);

  /**
   * 单位转换器
   */
  public final Function<Double, Double> converter;


  IndentUnitEnum(Function<Double, Double> converter) {
    this.converter = converter;
  }
}

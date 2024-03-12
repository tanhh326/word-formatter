package com.crane.wordformat.formatter.dto;

import com.crane.wordformat.formatter.enums.IndentUnitEnum;
import com.crane.wordformat.formatter.enums.LineSpacingRuleEnum;
import com.crane.wordformat.formatter.enums.SpacingUnitEnum;
import lombok.Data;

/**
 * 前端配置的样式模板
 */
@Data
public class StyleTemplateDto {

  public Font font;

  public ParagraphFormat paragraphFormat;


  @Data
  public static class Spacing {

    private Config before;
    private Config after;
    private Config line;

    @Data
    public static class Config {

      private LineSpacingRuleEnum type;

      private SpacingUnitEnum unit;

      private double value;
    }
  }

  @Data
  public static class Indent {

    private Config left;
    private Config right;
    private Config firstLine;

    @Data
    public static class Config {

      private IndentUnitEnum unit;

      private double value;
    }
  }

  @Data
  public class Font {

    private String nameFarEast;

    private String nameAscii;

    private double size;
  }

  @Data
  public class ParagraphFormat {

    private int alignment;

    private Indent indent;

    private Spacing spacing;
  }

}

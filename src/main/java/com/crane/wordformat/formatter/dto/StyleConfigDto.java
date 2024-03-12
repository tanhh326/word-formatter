package com.crane.wordformat.formatter.dto;

import com.crane.wordformat.formatter.enums.SectionEnums;
import lombok.Data;

/**
 * 样式配置
 */
@Data
public class StyleConfigDto {

  /**
   * 说明（key）
   */
  private SectionEnums desc;

  /**
   * 标题样式
   */
  private StyleTemplateDto title;

  /**
   * 正文样式
   */
  private StyleTemplateDto text;

  /**
   * 表格
   */
  private StyleTemplateDto table;

  /**
   * 图题
   */
  private StyleTemplateDto diagramTitle;

  /**
   * 表题
   */
  private StyleTemplateDto tableTitle;

  /**
   * 资料来源
   */
  private StyleTemplateDto source;

  /**
   * 脚注
   */
  private StyleTemplateDto footnote;
}

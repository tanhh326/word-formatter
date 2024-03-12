package com.crane.wordformat.formatter.global;

import com.aspose.words.Document;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import java.util.List;
import lombok.Data;

/**
 * 整个格式化过程共享的变量
 */
@Data
public class FormattingProcessShareVar {

  /**
   * 当前正在处理的章节，用于判断当前处理的是哪个章节，然后设置不同的正文的格式
   */
  private SectionEnums currentSection;

  /**
   * 学生论文文档（待格式化的文档）
   */
  private Document studetDocument;

  /**
   * 样式配置
   */
  private List<StyleConfigDto> styleConfigList;

  public FormattingProcessShareVar(List<StyleConfigDto> styleConfigList, Document studetDocument) {
    this.styleConfigList = styleConfigList;
    this.studetDocument = studetDocument;
  }

  public StyleConfigDto getStyleConfig(SectionEnums sectionEnums) {
    return styleConfigList.stream().filter(it -> it.getDesc() == sectionEnums).findFirst()
        .orElseThrow(() -> new RuntimeException("没有找到此配置！"));
  }
}

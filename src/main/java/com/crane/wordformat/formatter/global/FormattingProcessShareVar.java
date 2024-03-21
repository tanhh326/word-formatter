package com.crane.wordformat.formatter.global;

import com.aspose.words.Document;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.DegreeEnums;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.restful.dto.FormatProcessDTO;
import com.crane.wordformat.restful.entity.FormatConfigPO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private DegreeEnums degreeEnums;

  private FormatConfigPO formatConfigPO;

  private FormatProcessDTO formatProcessDTO;

  /**
   * 样式配置
   */
  private List<StyleConfigDto> styleConfigList;


  public FormattingProcessShareVar(Document studetDocument) {
    this.studetDocument = studetDocument;
  }

  public void setFormatConfigPO(FormatConfigPO formatConfigPO) {
    this.formatConfigPO = formatConfigPO;
    this.styleConfigList = new ObjectMapper().convertValue(
        formatConfigPO.getConfig(), new TypeReference<>() {
        });
  }

  public StyleConfigDto getStyleConfig(SectionEnums sectionEnums) {
    return styleConfigList.stream().filter(it -> it.getDesc() == sectionEnums).findFirst()
        .orElseThrow(() -> new RuntimeException("没有找到此配置！"));
  }
}

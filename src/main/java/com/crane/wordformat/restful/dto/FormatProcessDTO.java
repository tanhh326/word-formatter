package com.crane.wordformat.restful.dto;

import com.crane.wordformat.formatter.enums.DegreeEnums;
import com.crane.wordformat.restful.entity.CoverFormPO;
import java.util.Map;
import lombok.Data;

@Data
public class FormatProcessDTO {

  private DegreeEnums degree;

  private Cover zhCover;

  private Cover enCover;

  private Map<String, String> coverForm;

  private String formatConfigId;

  /**
   * 参考文献是否有序
   */
  private boolean referencesOrderly;

  @Data
  public static class Cover {

    private String id;

    private CoverFormPO coverFormPO;

    private Map<String, String> form;
  }
}
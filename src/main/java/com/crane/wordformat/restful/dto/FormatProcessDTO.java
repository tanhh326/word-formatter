package com.crane.wordformat.restful.dto;

import com.aspose.words.Document;
import com.crane.wordformat.formatter.InstructionsDissertationAuthorizationFormatter;
import com.crane.wordformat.formatter.enums.DegreeEnums;
import com.crane.wordformat.restful.entity.CoverFormPO;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FormatProcessDTO {

  private DegreeEnums degree;
  
  private String degreeCode;

  private Cover zhCover;

  private Cover enCover;

  private Map<String, String> coverForm;

  /**
   * {@link InstructionsDissertationAuthorizationFormatter#formatBody()}
   */
  private Document instructionsDissertationAuthorizationDoc;

  private String formatConfigId;

  private String originDocPath;

  private String originalFilename;

  /**
   * 参考文献是否有序
   */
  private boolean referencesOrderly;

  @Data
  @Accessors(chain = true)
  public static class Cover {

    private String id;

    private CoverFormPO coverFormPO;

    private Document document;

    private Map<String, String> form;
  }
}

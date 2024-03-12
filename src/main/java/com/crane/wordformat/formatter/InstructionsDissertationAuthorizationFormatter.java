package com.crane.wordformat.formatter;

import com.aspose.words.Body;
import com.aspose.words.Document;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.StyleIdentifier;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.MatchUtils;
import com.crane.wordformat.formatter.utils.StyleUtils;

/**
 * 目录部分只做识别，默认认为不可靠，删掉重新生成
 */
public class InstructionsDissertationAuthorizationFormatter extends AbstractFormatter {


  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());


  public InstructionsDissertationAuthorizationFormatter(
      FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.关于学位论文使用授权的说明;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String string = paragraph.toString(SaveFormat.TEXT);
    if (titles.isEmpty() &&
        StyleUtils.isNotToc(paragraph) &&
        MatchUtils.match(string, "关于学位论文使用授权的说明")
    ) {
      titles.add(StyleUtils.insertSectionBreakIfNotFirst(paragraph));
    }
  }

  @Override
  public void formatTitle() {
    if (titles.isEmpty()) {
      Document studetDocument = formattingProcessShareVar.getStudetDocument();
      Section section = new Section(studetDocument);
      studetDocument.getSections().add(section);
      Paragraph title = new Paragraph(studetDocument);
      Body body = new Body(studetDocument);
      body.getParagraphs().add(title);
      section.appendChild(body);
      title.getRuns().add(new Run(studetDocument, "关于学位论文使用授权的说明"));
      titles.add(title);
    }
    for (Paragraph title : titles) {
      StyleUtils.merge(title, styleConfigDto.getTitle(), StyleIdentifier.NORMAL);
      title.getListFormat().removeNumbers();
      for (Run run : title.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
      }
    }
  }

  @Override
  public void formatBody() {
    for (Paragraph title : titles) {
      for (Paragraph paragraph : title.getParentSection().getBody().getParagraphs()) {
        if (paragraph != title) {
          StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
          for (Run run : paragraph.getRuns()) {
            StyleUtils.merge(run.getFont(), styleConfigDto.getText());
          }
        }
      }
    }
  }
}

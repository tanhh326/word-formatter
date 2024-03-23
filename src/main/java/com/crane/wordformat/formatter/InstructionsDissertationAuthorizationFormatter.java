package com.crane.wordformat.formatter;

import com.aspose.words.Document;
import com.aspose.words.Paragraph;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
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

  }

  @Override
  public void formatBody() {
    Document temp = formattingProcessShareVar.getFormatProcessDTO()
        .getInstructionsDissertationAuthorizationDoc();
    Paragraph newTitle;
    if (titles.isEmpty()) {
      Document studetDocument = formattingProcessShareVar.getStudetDocument();
      Section section = new Section(studetDocument);
      section.appendContent(temp.getFirstSection());
      studetDocument.getSections().add(section);
      newTitle = section.getBody().getFirstParagraph();
    } else {
      Paragraph title = titles.get(0);
      Section parentSection = title.getParentSection();
      parentSection.clearContent();
      parentSection.getBody().getParagraphs().clear();
      parentSection.appendContent(temp.getFirstSection());
      newTitle = parentSection.getBody().getFirstParagraph();
    }
    this.titles.clear();
    titles.add(newTitle);
  }
}

package com.crane.wordformat.formatter;

import com.aspose.words.Body;
import com.aspose.words.Document;
import com.aspose.words.LineSpacingRule;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.StyleIdentifier;
import com.aspose.words.Underline;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.MatchUtils;
import com.crane.wordformat.formatter.utils.StyleUtils;

public class StatementFormatter extends AbstractFormatter {


  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());


  public StatementFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.声明;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (titles.isEmpty() &&
        StyleUtils.isNotToc(paragraph) &&
        MatchUtils.match(text, "声明")
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
      title.getRuns().add(new Run(studetDocument, "声明"));
      titles.add(title);
    }
    for (Paragraph title : titles) {
      StyleUtils.merge(title, styleConfigDto.getTitle(), StyleIdentifier.HEADING_1);
      title.getListFormat().removeNumbers();
      for (Run run : title.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
      }

    }
  }

  @Override
  public void formatBody() throws Exception {
    for (Paragraph title : titles) {
      for (Paragraph paragraph : title.getParentSection().getBody().getParagraphs()) {
        if (paragraph != title) {
          if (paragraph.toString(SaveFormat.TEXT).trim().matches("^签\\s*名.*")) {
            ParagraphFormat paragraphFormat = paragraph.getParagraphFormat();
            paragraphFormat.setAlignment(ParagraphAlignment.RIGHT);
            paragraphFormat.setLeftIndent(0);
            paragraphFormat.setFirstLineIndent(0);
            paragraphFormat.setCharacterUnitFirstLineIndent(0);
            paragraphFormat.setSpaceBefore(6);
            paragraphFormat.setLineSpacingRule(LineSpacingRule.EXACTLY);
            paragraphFormat.setLineSpacing(18);
          } else {
            StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
          }
          for (Run run : paragraph.getRuns()) {
            int underline = run.getFont().getUnderline();
            StyleUtils.merge(run.getFont(), styleConfigDto.getText());
            if (underline == Underline.SINGLE) {
              run.getFont().setUnderline(underline);
            }
          }
        }
      }
    }
  }
}

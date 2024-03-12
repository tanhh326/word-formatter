package com.crane.wordformat.formatter;

import com.aspose.words.Body;
import com.aspose.words.ControlChar;
import com.aspose.words.Document;
import com.aspose.words.Node;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Run;
import com.aspose.words.RunCollection;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.StyleIdentifier;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.ConverterUtils;
import com.crane.wordformat.formatter.utils.StyleUtils;
import java.util.ArrayList;
import java.util.List;

public class EnAbstractFormatter extends AbstractFormatter {


  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());


  public EnAbstractFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.英文摘要;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (titles.isEmpty() &&
        StyleUtils.isNotToc(paragraph) &&
        (text.matches(ConverterUtils.keywordToRegex("英文摘要")) ||
            text.matches(ConverterUtils.keywordToRegex("Abstract")))
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
      title.getRuns().add(new Run(studetDocument, "Abstract"));
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
    List<Node> removeNodes = new ArrayList<>();
    for (Paragraph title : titles) {
      for (Paragraph paragraph : title.getParentSection().getBody().getParagraphs()) {
        if (paragraph != title) {
          String text = paragraph.toString(SaveFormat.TEXT).trim();
          if (text.isBlank()) {
            removeNodes.add(paragraph);
          }
          StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
          // 关键词
          Run keywordRun = null;
          if (text.matches("^\\s*[Kk]\\s*e\\s*y\\s*w\\s*o\\s*r\\s*d[s*][:：]\\s*.*$")) {
            String keywordText = text.replaceFirst("^\\s*[Kk]eyword[s*][:：]\\s*", "");
            RunCollection runs = paragraph.getRuns();
            runs.clear();
            runs.add(new Run(paragraph.getDocument(), ControlChar.LINE_BREAK));
            keywordRun = new Run(paragraph.getDocument(), "Keywords:");
            runs.add(keywordRun);
            runs.add(new Run(paragraph.getDocument(), keywordText));
            ParagraphFormat paragraphFormat = paragraph.getParagraphFormat();
            paragraphFormat.setAlignment(ParagraphAlignment.LEFT);
            paragraphFormat.setLeftIndent(0);
            paragraphFormat.setCharacterUnitLeftIndent(0);
          }
          for (Run run : paragraph.getRuns()) {
            StyleUtils.merge(run.getFont(), styleConfigDto.getText());
            if (run == keywordRun) {
              keywordRun.getFont().setBold(true);
            }
          }
        }
      }
    }
    removeNodes.forEach(it -> it.remove());
  }
}

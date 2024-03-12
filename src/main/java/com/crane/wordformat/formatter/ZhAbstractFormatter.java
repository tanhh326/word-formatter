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

public class ZhAbstractFormatter extends AbstractFormatter {

  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());


  public ZhAbstractFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.中文摘要;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (titles.isEmpty() && (text.matches(ConverterUtils.keywordToRegex("中文摘要")) ||
        text.matches(ConverterUtils.keywordToRegex("摘要")))
    ) {
      titles.add(StyleUtils.insertSectionBreakIfNotFirst(paragraph));
    }
  }

  @Override
  public void formatTitle() {
    Document studetDocument = formattingProcessShareVar.getStudetDocument();
    Run newRun = new Run(studetDocument, "摘  要");
    if (titles.isEmpty()) {
      Section section = new Section(studetDocument);
      studetDocument.getSections().add(section);
      Paragraph title = new Paragraph(studetDocument);
      Body body = new Body(studetDocument);
      body.getParagraphs().add(title);
      section.appendChild(body);
      title.getRuns().add(newRun);
      titles.add(title);
    }
    for (Paragraph title : titles) {
      StyleUtils.merge(title, styleConfigDto.getTitle(), StyleIdentifier.HEADING_1);
      title.getListFormat().removeNumbers();
      title.getRuns().clear();
      title.getRuns().add(newRun);
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
          if (text.matches("^\\s*关\\s*键\\s*词\\s*[:：]\\s*.*$")) {
            String keywordText = text.replaceFirst("^\\s*关键词[:：]\\s*", "");
            RunCollection runs = paragraph.getRuns();
            runs.clear();
            // 和前一个段落空一行，注意：不是空一个段落
            runs.add(new Run(paragraph.getDocument(), ControlChar.LINE_BREAK));
            keywordRun = new Run(paragraph.getDocument(), "关键词：");
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
              keywordRun.getFont().setName("黑体");
            }
          }
        }
      }
    }
    removeNodes.forEach(it -> it.remove());
  }
}

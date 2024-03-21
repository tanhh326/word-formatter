package com.crane.wordformat.formatter;

import com.aspose.words.Body;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBase;
import com.aspose.words.Field;
import com.aspose.words.List;
import com.aspose.words.ListLevel;
import com.aspose.words.ListTemplate;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import com.aspose.words.RunCollection;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.StyleIdentifier;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.MatchUtils;
import com.crane.wordformat.formatter.utils.StyleUtils;

public class ReferencesFormatter extends AbstractFormatter {

  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());


  public ReferencesFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.参考文献;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (titles.isEmpty() && StyleUtils.isNotToc(paragraph) && MatchUtils.match(text, "参考文献")) {
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
      title.getRuns().add(new Run(studetDocument, "参考文献"));
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
      List list = title.getDocument().getLists().add(ListTemplate.NUMBER_DEFAULT);
      ListLevel listLevel = list.getListLevels().get(0);
      listLevel.setNumberFormat("[\u0000]");
      listLevel.setTabPosition(ConvertUtil.millimeterToPoint(10));
      listLevel.setTextPosition(ConvertUtil.millimeterToPoint(10));
      listLevel.setNumberPosition(0);
      for (Paragraph paragraph : title.getParentSection().getBody().getParagraphs()) {
        if (paragraph == title) {
          continue;
        }
        String text = paragraph.toString(SaveFormat.TEXT).trim();
        paragraph.getListFormat().removeNumbers();
        for (Field field : paragraph.getRange().getFields()) {
          field.unlink();
        }
        StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
        // 悬挂缩进设置完后，会带有左缩进，需要将左缩进设为悬挂锁紧的负值才会为0
        // https://forum.aspose.com/t/paragraphformat-firstlineindent-hanging-indent-sets-the-left-indent-as-negative-using-java/55487/5
        // @Deprecated 如果有自动编号或手写编号，清除重新生成 text.matches("^\\[\\s*\\d+\\s*\\].*$"
        DocumentBase document = paragraph.getDocument();
        RunCollection runs = paragraph.getRuns();
        runs.clear();
        runs.add(new Run(document,
            text.replaceFirst("^\\[\\s*\\d+\\s*\\]\\s*", "").trim()));
        if (formattingProcessShareVar.getFormatProcessDTO().isReferencesOrderly()) {
          paragraph.getListFormat().setList(list);
          paragraph.getListFormat().getListLevel().setNumberPosition(0);
        }
        for (Run run : paragraph.getRuns()) {
          StyleUtils.merge(run.getFont(), styleConfigDto.getText());
        }
      }
    }
  }
}

package com.crane.wordformat.formatter;

import com.aspose.words.Body;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.Style;
import com.aspose.words.StyleIdentifier;
import com.aspose.words.TabAlignment;
import com.aspose.words.TabLeader;
import com.aspose.words.TabStop;
import com.aspose.words.TabStopCollection;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.StyleUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * 目录部分只做识别，默认认为不可靠，删掉重新生成
 */
public class DirectoryFormatter extends AbstractFormatter {


  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());
  private final Map<Integer, StyleConfigDto> styleConfigMap = new HashMap<>() {{
    put(StyleIdentifier.TOC_1, formattingProcessShareVar.getStyleConfig(SectionEnums.一级目录));
    put(StyleIdentifier.TOC_2, formattingProcessShareVar.getStyleConfig(SectionEnums.二级目录));
    put(StyleIdentifier.TOC_3, formattingProcessShareVar.getStyleConfig(SectionEnums.三级目录));
  }};


  public DirectoryFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.目录;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (titles.isEmpty() && StyleUtils.isNotToc(paragraph) && text
        .matches("^\\s*目\\s*录\\s*$")) {
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
      title.getRuns().add(new Run(studetDocument, "目录"));
      titles.add(title);
    }
    for (Paragraph title : titles) {
      StyleUtils.merge(title, styleConfigDto.getTitle(), StyleIdentifier.HEADING_1);
      title.getListFormat().removeNumbers();
      for (Run run : title.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
      }
    }
    resetToc((Document) titles.get(0).getDocument());
  }

  private void resetToc(Document document) {
    styleConfigMap.forEach((k, v) -> {
      Style style = document.getStyles().getByStyleIdentifier(k);
      ParagraphFormat paragraphFormat = style.getParagraphFormat();
      TabStopCollection tabStops = paragraphFormat.getTabStops();
      tabStops.clear();
      // A4纸大小（21） - 页左右边距（3*2 ）= 15
      tabStops.add(
          new TabStop(ConvertUtil.millimeterToPoint(150), TabAlignment.RIGHT, TabLeader.DOTS));
      StyleUtils.merge(paragraphFormat, v.getTitle());
      StyleUtils.merge(paragraphFormat.getStyle().getFont(), v.getTitle());
      StyleUtils.merge(style.getFont(), v.getTitle());
    });
  }

  @Override
  public void formatBody() throws Exception {
    for (Paragraph title : titles) {
      Section section = title.getParentSection();
      NodeCollection<Node> nodes = section.getBody()
          .getChildNodes();
      for (Node node : nodes) {
        if (node != title) {
          node.remove();
        }
      }
      Document document = (Document) title.getDocument();
      DocumentBuilder documentBuilder = new DocumentBuilder(document);
      documentBuilder.moveTo(title);
      Paragraph paragraph = documentBuilder.insertParagraph();
      documentBuilder.moveTo(paragraph);
      documentBuilder.insertTableOfContents("\\o \"1-3\" \\h \\z \\u");
      // document.updateFields();
      /*for (Field field : paragraph.getRange().getFields()) {
        field.update(true);
      }
      for (Paragraph newP : section.getBody().getParagraphs()) {
        if (newP != title) {
          for (Run run : newP.getRuns()) {
            run.getFont().clearFormatting();
          }
        }
      }*/
    }
  }
}

package com.crane.wordformat.formatter;

import com.aspose.words.Body;
import com.aspose.words.Cell;
import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.Document;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.PreferredWidth;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.StyleIdentifier;
import com.aspose.words.Table;
import com.aspose.words.TableAlignment;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.MatchUtils;
import com.crane.wordformat.formatter.utils.StyleUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 目录部分只做识别，默认认为不可靠，删掉重新生成
 */
public class PublicReviewerDefenseCommitteeListsFormatter extends AbstractFormatter {

  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());


  public PublicReviewerDefenseCommitteeListsFormatter(
      FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.学位论文公开评阅人和答辩委员会名单;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (titles.isEmpty() &&
        StyleUtils.isNotToc(paragraph) &&
        (MatchUtils.match(text, "学位论文指导小组、公开评阅人和答辩委员会名单") ||
            MatchUtils.match(text, "学位论文公开评阅人和答辩委员会名单"))
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
      title.getRuns().add(new Run(studetDocument, "学位论文指导小组、公开评阅人和答辩委员会名单"));
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
  public void formatBody() throws Exception {
    List<Paragraph> removeParagraphs = new ArrayList<>();
    for (Paragraph title : titles) {
      NodeCollection<Node> nodes = title.getParentSection().getBody().getChildNodes();
      for (Node node : nodes) {
        if (node == title) {
          continue;
        }
        if (node instanceof Paragraph paragraph) {
          if (paragraph.getChildNodes().iterator().hasNext()) {
            formatParagraph(paragraph);
          } else {
            removeParagraphs.add(paragraph);
          }
        } else if (node instanceof Table table) {
          formatTable(table);
        }
      }
    }
    for (Paragraph paragraph : removeParagraphs) {
      paragraph.remove();
    }
  }

  private void formatParagraph(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (MatchUtils.match(text, "指导小组名单")
        || MatchUtils.match(text, "公开评阅人名单")
        || MatchUtils.match(text, "答辩委员会名单")) {
      StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
      for (Run run : paragraph.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getText());
      }
    } else {
      StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
      for (Run run : paragraph.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getText());
      }
    }
  }

  private void formatTable(Table table) throws Exception {
    NodeCollection<Node> nodes = table.getChildNodes(NodeType.ANY, true);
    for (Node node : nodes) {
      if (node instanceof Cell cell) {
        cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
      } else if (node instanceof Paragraph paragraph) {
        StyleUtils.merge(paragraph, styleConfigDto.getTable(), StyleIdentifier.NORMAL);
        for (Run run : paragraph.getRuns()) {
          StyleUtils.merge(run.getFont(), styleConfigDto.getTable());
        }
      }
    }
    table.setLeftIndent(0);
    table.setHorizontalAnchor(RelativeHorizontalPosition.COLUMN);
    // 居中、wps和微软word有差异
    table.setAlignment(TableAlignment.CENTER);
    table.setPreferredWidth(PreferredWidth.fromPercent(100));
  }
}

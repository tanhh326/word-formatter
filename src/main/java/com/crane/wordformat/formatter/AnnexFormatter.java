package com.crane.wordformat.formatter;

import com.aspose.words.Cell;
import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.PreferredWidth;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.StyleIdentifier;
import com.aspose.words.Table;
import com.aspose.words.TableAlignment;
import com.crane.wordformat.formatter.constant.TheConstant;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.StyleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnexFormatter extends AbstractFormatter {

  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());

  public AnnexFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.附录;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT);
    if (StyleUtils.isNotToc(paragraph) && text.matches("^(?s)\\s*附\\s*录\\s*[A-Z].*\n$")) {
      titles.add(StyleUtils.insertSectionBreakIfNotFirst(paragraph));
    }
  }

  private List<String> isFigureTitle(Paragraph paragraph) {
    int emptyCount = 0;
    List<String> matched = new ArrayList<>();
    for (Run run : paragraph.getRuns()) {
      if (run.getText().isBlank()) {
        emptyCount++;
      }
    }
    Pattern pattern = Pattern.compile("([图表]\\s*\\d+[\\.-]\\d+\\s*[　\\s]*)");
    Matcher matcher = pattern.matcher(paragraph.getText());
    while (matcher.find()) {
      matched.add(matcher.group(1));
    }
    return emptyCount > 0 ? matched : null;
  }

  @Override
  public void formatTitle() throws Exception {
    for (Paragraph title : titles) {
      StyleUtils.merge(title, styleConfigDto.getTitle(), StyleIdentifier.HEADING_1);
      String text = title.toString(SaveFormat.TEXT).replaceAll(" ", "")
          .replaceFirst("([A-Z])", "$1" + TheConstant.ZH_SPACE);
      title.getRuns().clear();
      title.getRuns().add(new Run(title.getDocument(), text.trim()));
      title.getListFormat().removeNumbers();
      for (Run run : title.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
      }
    }
  }

  @Override
  public void formatBody() throws Exception {
    for (Paragraph title : titles) {
      NodeCollection<Node> nodes = title.getParentSection().getBody().getChildNodes();
      for (Node node : nodes) {
        if (node == title) {
          continue;
        }
        if (node instanceof Paragraph paragraph) {
          String text = paragraph.toString(SaveFormat.TEXT);
          if (paragraph.getParagraphFormat().getAlignment() == ParagraphAlignment.CENTER) {
            formatShapeTableTitle(paragraph, text);
          } else {
            StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
            for (Run run : paragraph.getRuns()) {
              StyleUtils.merge(run.getFont(), styleConfigDto.getText());
            }
          }
        } else if (node instanceof Table table) {
          formatTable(table);
        }
      }
    }
  }

  private void formatShapeTableTitle(Paragraph paragraph, String text) {
    if (text.trim().startsWith("表") || text.trim().startsWith("续表")) {
      StyleUtils.merge(paragraph, styleConfigDto.getTableTitle(), StyleIdentifier.NORMAL);
      for (Run run : paragraph.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getTableTitle());
      }
    } else if (text.trim().startsWith("图")) {
      StyleUtils.merge(paragraph, styleConfigDto.getDiagramTitle(),
          StyleIdentifier.NORMAL);
      for (Run run : paragraph.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getDiagramTitle());
      }
    } else if (text.trim().startsWith("注：") || text.trim().startsWith("*注：")) {
      StyleUtils.merge(paragraph, styleConfigDto.getSource(),
          StyleIdentifier.NORMAL);
      for (Run run : paragraph.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getSource());
      }
    } else {
      // 研究项目简介 简介 等标题
      StyleConfigDto styleConfig = formattingProcessShareVar.getStyleConfig(
          SectionEnums.一级节标题);
      StyleUtils.merge(paragraph, styleConfig.getTitle(),
          StyleIdentifier.NORMAL);
      for (Run run : paragraph.getRuns()) {
        StyleUtils.merge(run.getFont(), styleConfig.getTitle());
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
    table.setAlignment(TableAlignment.CENTER);
    table.setPreferredWidth(PreferredWidth.fromPercent(100));
  }
}

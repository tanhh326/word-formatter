package com.crane.wordformat.formatter;

import com.aspose.words.Border;
import com.aspose.words.Cell;
import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FieldChar;
import com.aspose.words.FieldType;
import com.aspose.words.Font;
import com.aspose.words.Footnote;
import com.aspose.words.HorizontalAlignment;
import com.aspose.words.LineSpacingRule;
import com.aspose.words.LineStyle;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.PreferredWidth;
import com.aspose.words.RelativeHorizontalPosition;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.RunCollection;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import com.aspose.words.Shape;
import com.aspose.words.SpecialChar;
import com.aspose.words.StyleIdentifier;
import com.aspose.words.Table;
import com.aspose.words.TableAlignment;
import com.aspose.words.WrapType;
import com.crane.wordformat.formatter.constant.TheConstant;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.utils.ConverterUtils;
import com.crane.wordformat.formatter.utils.StyleUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;


public class ChapterFormatter extends AbstractFormatter {

  private final List<Pattern> nodeTitlePatterns = Arrays.asList(
      Pattern.compile("^\\s*([一二三四五六七八九十〇]+、).*$"),
      Pattern.compile("^\\s*(（[一二三四五六七八九十〇]+）).*$"),
      Pattern.compile("^\\s*(\\([一二三四五六七八九十〇]+\\)).*$"),
      Pattern.compile("^\\s*(\\d+\\.).*$")
  );

  private final StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
      sectionEnums());

  private NodeTitleOfRegex nodeTitleOfRegex;

  public ChapterFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    super(formattingProcessShareVar);
  }

  @Override
  public SectionEnums sectionEnums() {
    return SectionEnums.章节;
  }

  private double getParagraphMaxFontSize(Paragraph paragraph) {
    double max = 0;
    for (Run run : paragraph.getRuns()) {
      if (run.getFont().getSize() > max) {
        max = run.getFont().getSize();
      }
    }
    return max;
  }

  @Override
  public void confirmTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT).trim();
    if (StyleUtils.isNotToc(paragraph) &&
        // 排除正文当中出现匹配的字符
        !text.contains("。") && !text.contains("，") &&
        (
            text.matches("^\\s*第\\s*(\\d|[〇一二三四五六七八九十]+?)\\s*章.+$")
                ||
                (
                    paragraph.getParagraphFormat().getAlignment() == ParagraphAlignment.CENTER
                        && text.matches("^\\d+\\s*.*")
                        && text.length() < 20
                        // ≥ 三号字体
                        && getParagraphMaxFontSize(paragraph) >= 16
                )
        )
    ) {
      titles.add(StyleUtils.insertSectionBreakIfNotFirst(paragraph));
    }
  }

  /**
   * Matcher matcher2 = Pattern.compile("\\s*(\\d+)\\s*.*$").matcher(text); if (matcher2.find()) {
   * String numberStr = matcher2.group(1); chapterNumber = Integer.parseInt(numberStr); text =
   * text.replaceFirst(numberStr, "第" + numberStr + "章"); }
   */
  @Override
  public void formatTitle() throws Exception {
    for (Paragraph title : titles) {
      String text = title.toString(SaveFormat.TEXT).replaceAll(" ", "")
          .replaceFirst("(章)", "$1" + TheConstant.ZH_SPACE);
      StyleUtils.merge(title, styleConfigDto.getTitle(), StyleIdentifier.HEADING_1);
      RunCollection runs = title.getRuns();
      Pattern pattern = Pattern.compile("第(.*?)章");
      Matcher matcher = pattern.matcher(text);
      // 这里肯定能找到的，不用判断了
      StringBuffer result = new StringBuffer();
      if (matcher.find()) {
        String chineseNumber = matcher.group(1);
        if (chineseNumber.matches("\\d+")) {
          result.append(text);
        } else {
          int zhToNum = ConverterUtils.zhToNum(chineseNumber);
          matcher.appendReplacement(result, "第" + zhToNum + "章");
          matcher.appendTail(result);
        }
      } else {
        // 采用第二中方案 (1 绪论)
        Matcher matcher2 = Pattern.compile("\\s*(\\d+)\\s*.*$").matcher(text);
        if (matcher2.find()) {
          String numberStr = matcher2.group(1);
          result.append(
              text.replaceFirst(numberStr, "第" + numberStr + "章" + TheConstant.ZH_SPACE));
        }
      }

      title.getListFormat().removeNumbers();
      runs.clear();
      // 此处要去掉原来的分段标记 .trim()，否则会出现两行
      runs.add(new Run(title.getDocument(), result.toString().trim()));
      for (Run run : runs) {
        StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
      }

    }
  }

  private void formatFootnote(Section section) throws Exception {
    NodeCollection<Footnote> footnotes = section.getChildNodes(NodeType.FOOTNOTE, true);
    List<Node> removeNodes = new ArrayList<>();
    for (Footnote footnote : footnotes) {
      Font font = footnote.getFont();
      StyleUtils.merge(font, styleConfigDto.getFootnote());
      font.setSuperscript(true);
      for (Paragraph paragraph : footnote.getParagraphs()) {
        if (paragraph.getChildNodes().iterator().hasNext()) {
          StyleUtils.merge(paragraph, styleConfigDto.getFootnote(), StyleIdentifier.NORMAL);
          String text = paragraph.toString(SaveFormat.TEXT);

          NodeCollection<Node> paragraphChildNodes = paragraph.getChildNodes(NodeType.ANY, true);
          for (Node node : paragraphChildNodes) {
            // 此处不要调换顺序，因为FieldChar是SpecialChar的子类
            if (node instanceof FieldChar fieldChar) {
              // 去除超链接标记，否则会在 document.updateFields() 时产生 Error! Hyperlink reference not valid. ，
              // 并且这个错误会显示到文档中
              if (fieldChar.getFieldType() == FieldType.FIELD_HYPERLINK) {
                removeNodes.add(fieldChar);
              }
            } else if (node instanceof SpecialChar specialChar) {
              // 脚注内容编号
              StyleUtils.merge(specialChar.getFont(), styleConfigDto.getFootnote());
            }
          }
          paragraph.getRuns().clear();
          Run run = new Run(section.getDocument(), " " + text.trim());
          StyleUtils.merge(run.getFont(), styleConfigDto.getFootnote());
          paragraph.getRuns().add(run);
        } else {
          removeNodes.add(paragraph);
        }
      }
    }
    removeNodes.forEach(it -> it.remove());
  }

  @Override
  public void formatBody() throws Exception {
    List<Paragraph> removeParagraphs = new ArrayList<>();
    int chapterNumber = 0;
    for (Paragraph title : titles) {
      nodeTitleOfRegex = new NodeTitleOfRegex(++chapterNumber);
      Section section = title.getParentSection();
      preFormatShape(section);
      formatFootnote(section);
      NodeCollection<Node> bodyChildNodes = section.getBody().getChildNodes();
      for (Node node : bodyChildNodes) {
        if (node == title) {
          continue;
        }
        if (node instanceof Paragraph paragraph) {
          String text = paragraph.toString(SaveFormat.TEXT);
          if (formatNodeTitle(paragraph)) {
            continue;
          }
          if (!paragraph.getChildNodes().iterator().hasNext()) {
            removeParagraphs.add(paragraph);
          } else {
            // 居中的为图题、标题、资料来源
            if (paragraph.getParagraphFormat().getAlignment() == ParagraphAlignment.CENTER) {
              formatShapeTableTitle(paragraph, text);
            } else {
              // 前面有空格是缩进，除非加了空格Unicode编码
              paragraph.getRange().replace("^\\s+", "");
              StyleUtils.merge(paragraph, styleConfigDto.getText(), StyleIdentifier.NORMAL);
              for (Run run : paragraph.getRuns()) {
                StyleUtils.merge(run.getFont(), styleConfigDto.getText());
              }
            }
          }
        } else if (node instanceof Table table) {
          formatTable(table);
        }
      }
    }
    removeParagraphs.forEach(it -> it.remove());
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
    }
  }

  private Row findTableHead(Table table) throws Exception {
    for (Row row : table.getRows()) {
      if (row.getCells().getCount() > 1 && !row.toString(SaveFormat.TEXT).isBlank()) {
        return row;
      }
    }
    return null;
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
    table.clearBorders();
    for (Row row : table.getRows()) {
      for (Cell cell : row.getCells()) {
        cell.getCellFormat().getBorders().clearFormatting();
      }
    }
    Row tableHeadRow = findTableHead(table);
    if (tableHeadRow != null) {
      for (Cell cell : tableHeadRow.getCells()) {
        Border top = cell.getCellFormat().getBorders().getTop();
        top.setLineStyle(LineStyle.SINGLE);
        top.setColor(Color.black);
        top.setLineWidth(1.5);
        Border bottom = cell.getCellFormat().getBorders().getBottom();
        bottom.setLineStyle(LineStyle.SINGLE);
        bottom.setColor(Color.black);
        bottom.setLineWidth(1);
      }
      for (Cell cell : table.getLastRow().getCells()) {
        Border bottom = cell.getCellFormat().getBorders().getBottom();
        bottom.setLineStyle(LineStyle.SINGLE);
        bottom.setColor(Color.black);
        bottom.setLineWidth(1.5);
      }
    }
  }

  private void preFormatShape(Section section) {
    Paragraph[] array = section.getBody().getParagraphs().toArray();
    for (Paragraph paragraph : array) {
      NodeCollection<Shape> shapes = paragraph.getChildNodes(NodeType.SHAPE, false);
      if (!shapes.iterator().hasNext()) {
        continue;
      }
      if (shapes.getCount() == 1) {
        Shape shape = (Shape) shapes.get(0);
        shape.setWrapType(WrapType.INLINE);
        shape.setHorizontalAlignment(HorizontalAlignment.CENTER);
        DocumentBuilder documentBuilder = new DocumentBuilder((Document) section.getDocument());
        documentBuilder.moveTo(paragraph);
        Paragraph newParagraph = documentBuilder.insertParagraph();
        ParagraphFormat paragraphFormat = newParagraph.getParagraphFormat();
        paragraphFormat.clearFormatting();
        paragraphFormat.setLineSpacingRule(LineSpacingRule.MULTIPLE);
        paragraphFormat.setLineSpacing(12);
        paragraphFormat.setAlignment(ParagraphAlignment.CENTER);
        paragraphFormat.setSpaceBefore(12);
        paragraphFormat.setSpaceAfter(6);
        newParagraph.appendChild(shape);
      } else {
        ParagraphFormat paragraphFormat = paragraph.getParagraphFormat();
        paragraphFormat.clearFormatting();
        paragraphFormat.setLineSpacingRule(LineSpacingRule.MULTIPLE);
        paragraphFormat.setLineSpacing(12);
        paragraphFormat.setAlignment(ParagraphAlignment.CENTER);
        paragraphFormat.setSpaceBefore(12);
        paragraphFormat.setSpaceAfter(6);
        /*
          // todo 多张图片时处理下面的图题段落居中，假如下面的不是图题？？
          if (paragraph.getNextSibling() instanceof Paragraph nextParagraph) {
          ParagraphFormat nextParagraphFormat = nextParagraph.getParagraphFormat();
          nextParagraphFormat.clearFormatting();
          nextParagraphFormat.setLineSpacingRule(LineSpacingRule.MULTIPLE);
          nextParagraphFormat.setLineSpacing(12);
          nextParagraphFormat.setAlignment(ParagraphAlignment.CENTER);
        }*/
      }
    }
  }

  private boolean formatNodeTitle(Paragraph paragraph) throws Exception {
    String text = paragraph.toString(SaveFormat.TEXT).trim();
    paragraph.getListFormat().removeNumbers();
    if (text.contains("。") || text.contains("；") || text.contains(";")) {
      return false;
    }
    String finalText = "";
    Integer level = null;
    String splitter = "\\.";
    Pattern pattern = Pattern.compile("^(^\\d+\\.\\d+[\\.+\\d+]*)\\s*(.+)");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      String matchedNumber = matcher.group(1);
      level = matchedNumber.split(splitter).length;
      finalText = ConverterUtils.removeEmpty(text)
          .replaceFirst(matchedNumber, matchedNumber + TheConstant.ZH_SPACE);
    } else {
      for (Pattern patternIt : nodeTitlePatterns) {
        Matcher matcherIt = patternIt.matcher(text);
        if (matcherIt.find()) {
          String parse = nodeTitleOfRegex.parse(patternIt.pattern());
          level = parse.split(splitter).length;
          paragraph.getRuns().clear();
          // 错误编号格式转换
          finalText =
              ConverterUtils.removeEmpty(text)
                  .replaceFirst(matcherIt.group(1), parse + TheConstant.ZH_SPACE);
          break;
        }
      }
    }
    // 不是节标题，交给文本处理器处理
    if (level == null) {
      return false;
    }
    paragraph.getRuns().clear();
    paragraph.getRuns().add(new Run(paragraph.getDocument(), finalText.trim()));
    switch (level) {
      case 2 -> {
        // 最上面还有个一级章节标题,所以这里以二级标题开始
        StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
            SectionEnums.一级节标题);
        StyleUtils.merge(paragraph, styleConfigDto.getTitle(), StyleIdentifier.HEADING_2);
        for (Run run : paragraph.getRuns()) {
          StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
        }
      }
      case 3 -> {
        StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
            SectionEnums.二级节标题);
        StyleUtils.merge(paragraph, styleConfigDto.getTitle(), StyleIdentifier.HEADING_3);
        for (Run run : paragraph.getRuns()) {
          StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
        }
      }
      case 4 -> {
        StyleConfigDto styleConfigDto = formattingProcessShareVar.getStyleConfig(
            SectionEnums.三级节标题);
        StyleUtils.merge(paragraph, styleConfigDto.getTitle(), StyleIdentifier.HEADING_4);
        for (Run run : paragraph.getRuns()) {
          StyleUtils.merge(run.getFont(), styleConfigDto.getTitle());
        }
      }
      default -> {
      }
    }
    paragraph.getListFormat().removeNumbers();
    return true;
  }

  /**
   * 节标题类型多种多样，需要根据不同的正则匹配确定标题等级
   */
  @Data
  private class NodeTitleOfRegex {

    private final List<String> levelRegexList = new ArrayList<>();

    private final List<List<Integer>> levelNumberList = new ArrayList<>() {{
      add(new ArrayList<>());
      add(new ArrayList<>());
      add(new ArrayList<>());
    }};

    /**
     * 章节编号，需要拼接到节编号的最前方
     */
    private Integer chapterNumber;

    public NodeTitleOfRegex(int chapterNumber) {
      this.chapterNumber = chapterNumber;
    }

    /**
     * 根据不同的正则匹配确定标题等级
     *
     * @param levelRegex
     * @return
     */
    public String parse(String levelRegex) {
      if (!this.levelRegexList.contains(levelRegex)) {
        if (this.levelRegexList.size() == 3) {
          throw new RuntimeException("最多支持三级节标题");
        }
        this.levelRegexList.add(levelRegex);
      }
      int level = this.levelRegexList.indexOf(levelRegex);
      // 确定第几级
      List<Integer> arrays = levelNumberList.get(level);
      // 如果当前等级还没有编号，加一个
      if (arrays.size() == 0) {
        arrays.add(1);
      } else {
        arrays.add(arrays.get(arrays.size() - 1) + 1);
        // 某个等级数值提升，清空子级 1.2.2 -> 2
        for (int i = 0; i < levelNumberList.size(); i++) {
          if (i > level) {
            levelNumberList.get(i).clear();
          }
        }
      }
      return getLastNumber();
    }

    /**
     * 获取最后一级的编号
     *
     * @return
     */
    public String getLastNumber() {
      List<Integer> news = new ArrayList<>();
      news.add(this.chapterNumber);
      for (List<Integer> integers : levelNumberList) {
        if (!integers.isEmpty()) {
          news.add(integers.get(integers.size() - 1));
        }
      }
      return news.stream().map(String::valueOf).collect(Collectors.joining("."));
    }
  }
}

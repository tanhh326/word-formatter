package com.crane.wordformat.factory;

import com.aspose.words.Body;
import com.aspose.words.BreakType;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Field;
import com.aspose.words.FieldChar;
import com.aspose.words.FieldType;
import com.aspose.words.Font;
import com.aspose.words.Footnote;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphCollection;
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
import com.aspose.words.Table;
import com.aspose.words.TableAlignment;
import com.crane.wordformat.formatter.ReferencesFormatter;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ChapterFormatterTest {

  @Test
  public void testTitleGroup() {
    Pattern pattern = Pattern.compile("^([\\d.]+)\\s*(.+)");
    String str = "2.1.2 测试标题123";
    Matcher matcher = pattern.matcher(str);
    boolean find = matcher.find();
    String group1 = matcher.group(1);
    String group2 = matcher.group(2);
    Assertions.assertTrue(find);
    Assertions.assertEquals(2, matcher.groupCount());
  }

  @Test
  public void testTitleGroup2() {
    // 中文一组，其它一组
    String regex = "([a-zA-Z0-9.\\s]+|[^a-zA-Z0-9.\\s]+)";

    Pattern pattern = Pattern.compile(regex);

    Matcher matcher = pattern.matcher("1.1.2 Abv123标 22 题Ab测试");

    while (matcher.find()) {
      String text = matcher.group();
      //System.out.println(text);
      if (text.matches("[\\u4e00-\\u9fa5]+")) {
        System.out.println(text);
      }
    }
  }

  @Test
  public void testTitleGroup3() {
    // 中文一组，其它一组
    Pattern.compile("^\\s*参考文献\\s*$");

    String regex = "^\\s*参考文献\\s*$";
    System.out.println("  参考文献  123".matches(regex));
  }

  @Test
  public void testTitle() {
    String regex = "^\\s*中*\\s*文*\\s*摘\\s*要\\s*$";
    Assertions.assertTrue("中文摘要".matches(regex));
    Assertions.assertTrue("摘要".matches(regex));
    Assertions.assertTrue("中 文 摘  要".matches(regex));
    Assertions.assertFalse("摘药".matches(regex));
  }

  @Test
  public void test3() {
    String regex = "^\\s*第\\s*(\\d|[〇一二三四五六七八九十]+?)\\s*章[^：]*$";
    String s = "第2章期权概述对最主要的传统股票期权（欧式以及美式）进行简要介绍，以及不同发达市场交易所场内期权交易的市场规模，和流动性。由于场外股票期权交易主要基于欧式期权组合，本文会对传统欧式期权的主要定价方法进行介绍，同时简要描述表示期权风险的Greeks参数，为之后章节做简要铺垫";
    String s2 = "第 一十 章期权概述对最主要的传统股票期权（欧式以及美式）进行简要介绍，以及不同发达市场交易所场内期权交易的市场规模，和流动性。由于场外股票期权交易主要基于欧式期权组合，本文会对传统欧式期权的主要定价方法进行介绍，同时简要描述表示期权风险的Greeks参数，为之后章节做简要铺垫";
    Assertions.assertTrue(s.matches(regex));
    Assertions.assertTrue(Pattern.compile(regex).matcher(s).find());
    Assertions.assertTrue(s2.matches(regex));
    Assertions.assertTrue(Pattern.compile(regex).matcher(s2).find());
  }

  @Test
  public void test4() {
    String regex = "^第\\s*(.+?)\\s*章?.*$";
    String text = "第 一 章  章引言";
    Matcher matcher = Pattern.compile(regex).matcher(text);
    System.out.println(matcher.find());
    String group = matcher.group(0);
    String group1 = matcher.group(1);
    System.out.println(group);
    System.out.println(group1);
  }

  @Test
  public void test5() {
    String regex = "\\b\\d+(\\.\\d+)+\\b";
    String text = "1.3.3 1.2.4章节一";
    Matcher matcher = Pattern.compile(regex).matcher(text);
    if (matcher.find()) {
      System.out.println(matcher.group());
    }
  }

  @Test
  public void test6() {
    String regex = "^\\s*中*\\s*文*\\s*摘\\s*要$";
    String text = "  中文摘要";
    System.out.println(text.matches(regex));
  }

  @Test
  public void test7() {
    Pattern pattern1 = Pattern.compile("^\\s*(\\([一二三四五六七八九十〇]+\\)).*$");
    Pattern pattern2 = Pattern.compile("^\\s*(（[一二三四五六七八九十〇]+）).*$");
    Pattern pattern3 = Pattern.compile("^\\s*([一二三四五六七八九十〇]+、).*$");
    Pattern pattern4 = Pattern.compile("^\\s*(\\d+\\.).*$");

    Matcher matcher1 = pattern1.matcher("(一) 关于绿色金融的相关研究");
    Assertions.assertTrue(matcher1.find());
    Assertions.assertEquals(matcher1.group(1), "(一)");

    Matcher matcher2 = pattern2.matcher("（一） 关于绿色金融的相关研究");
    Assertions.assertTrue(matcher2.find());
    Assertions.assertEquals(matcher2.group(1), "（一）");

    Matcher matcher3 = pattern3.matcher("二、 关于绿色金融的相关研究");
    Assertions.assertTrue(matcher3.find());
    Assertions.assertEquals(matcher3.group(1), "二、");

    Matcher matcher4 = pattern4.matcher("12. 关于绿色金融的相关研究");
    Assertions.assertTrue(matcher4.find());
    Assertions.assertEquals(matcher4.group(1), "12.");
  }

  @Test
  public void test8() {
    String input = "啊ww我a n a你好世界，Hello word 。!a";

    String zhRegex = "[\\u4e00-\\u9fa5，。？！‘“]";
    String enNumberRegex = "[a-zA-Z\\d,\\.?!'\"\\s]";

    List<TextLanguageGroup> textLanguageGroups = new ArrayList<>();
    TextLanguageGroup currentGroup = new TextLanguageGroup();
    textLanguageGroups.add(currentGroup);
    for (char c : input.toCharArray()) {
      String charString = String.valueOf(c);
      if (charString.matches(zhRegex) && !currentGroup.isZh()) {
        currentGroup = new TextLanguageGroup();
        currentGroup.setZh(true);
        textLanguageGroups.add(currentGroup);
      } else if (charString.matches(enNumberRegex) && currentGroup.isZh()) {
        currentGroup = new TextLanguageGroup();
        currentGroup.setZh(false);
        textLanguageGroups.add(currentGroup);
      }
      currentGroup.getChars().add(c);
    }
    // 第一个元素可能没匹配到字符
    textLanguageGroups.stream().findFirst().ifPresent(it -> {
      if (it.getChars().isEmpty()) {
        textLanguageGroups.remove(0);
      }
    });
    System.out.println(textLanguageGroups);
  }


  @Test
  public void test9() {
    String enNumberRegex = "[a-zA-Z\\d,\\.?\\!'\"\\s]";
    Assertions.assertTrue("-".matches(enNumberRegex));
    /*Assertions.assertTrue("\\".matches(enNumberRegex));
    Assertions.assertTrue("/".matches(enNumberRegex));
    Assertions.assertTrue("$".matches(enNumberRegex));*/
  }

  @Test
  public void test10() {
    String enNumberRegex = "\\s*(\\d+)\\s*.*$";
    Assertions.assertTrue("1 数论".matches(enNumberRegex));
    Matcher matcher = Pattern.compile(enNumberRegex).matcher("1 数论");
    matcher.find();
    System.out.println(matcher.group(1));
    /*Assertions.assertTrue("\\".matches(enNumberRegex));
    Assertions.assertTrue("/".matches(enNumberRegex));
    Assertions.assertTrue("$".matches(enNumberRegex));*/
  }

  @Test
  public void test11() {
    Pattern pattern = Pattern.compile("([图表]\\s*\\d+[\\.-]\\d+\\s*[　\\s]*)");
    Matcher matcher = pattern.matcher(
        "图4.9　权益类基金与非权益类基金发展趋势     图4.10 权益类基金与非权益类基金规模对比");
    while (matcher.find()) {
      System.out.println(matcher.group(1));
    }
  }

  @Test
  public void test12() {
    System.out.println(
        "二〇二三年三月".matches(
            "\\s*[〇一二三四五六七八九十]{2,4}年[〇一二三四五六七八九十]{1,2}月\\s*"));
    System.out.println(
        "二〇二三年三月2".matches(
            "[〇一二三四五六七八九十]{2,4}年[〇一二三四五六七八九十]{1,2}月\\s*"));
  }

  @Test
  public void test13() throws Exception {

    Document document = new Document("C:\\Users\\thh\\Desktop\\你是谁who are you.docx");
    // 只会获取到body中的段落，不会获取到表格、脚注、页眉中的段落
    ParagraphCollection paragraphs1 = document.getFirstSection().getBody().getParagraphs();
    NodeCollection<Paragraph> paragraphs = document.getChildNodes(NodeType.PARAGRAPH, true);
    NodeCollection<Node> a = document.getChildNodes(NodeType.ANY, true);
    document.updateListLabels();
    for (Paragraph paragraph : paragraphs) {
      System.out.println(paragraph);
      String text = paragraph.toString(SaveFormat.TEXT);
      // 如果有自动编号。

      String formatted = text.replaceAll("附\\s*录\\s*(\\w)", "附录\u3000$1");
      Run run = new Run(document, formatted);
      run.getFont().setNameFarEast("宋体");
      run.getFont().setNameAscii("Arial");
      paragraph.getRuns().clear();
      paragraph.getRuns().add(run);
      if (paragraph.isListItem()) {
        paragraph.getListFormat().removeNumbers();
      }
    }
    document.save("C:\\Users\\thh\\Desktop\\你是谁.docx");
  }

  @Test
  public void test14() throws Exception {

    Document document = new Document("C:\\Users\\thh\\Desktop\\你是谁who are you.docx");
    // 只会获取到body中的段落，不会获取到表格、脚注、页眉中的段落
    ParagraphCollection paragraphs = document.getFirstSection().getBody().getParagraphs();
    for (Paragraph paragraph : paragraphs) {
      if (paragraph.getText().trim().contains("我在这呢")) {
        DocumentBuilder documentBuilder = new DocumentBuilder((Document) paragraph.getDocument());
        documentBuilder.moveTo(paragraph.getFirstChild());
        documentBuilder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
      }
    }
    document.save("C:\\Users\\thh\\Desktop\\你是谁.docx");
  }

  @Test
  public void test15() throws Exception {
    Document document = new Document("C:\\Users\\thh\\Desktop\\table.docx");
    // 只会获取到body中的段落，不会获取到表格、脚注、页眉中的段落
    Table table = document.getFirstSection().getBody()
        .getTables().get(0);
    table.setAlignment(TableAlignment.CENTER);
    document.save("C:\\Users\\thh\\Desktop\\你是谁.docx");

  }

  @Test
  public void test16() throws Exception {
    Document document = new Document("C:\\Users\\thh\\Desktop\\table.docx");
    NodeCollection<Footnote> nodes = document.getFirstSection()
        .getChildNodes(NodeType.FOOTNOTE, true);
    for (Footnote node : nodes) {
      for (Paragraph paragraph : node.getParagraphs()) {
        paragraph.getRuns().clear();
        Node[] array = paragraph.getChildNodes(NodeType.ANY, true).toArray();
        for (Node node1 : array) {
          System.out.println(node1);
          if (node1 instanceof FieldChar fieldChar) {
            if (fieldChar.getFieldType() == FieldType.FIELD_HYPERLINK) {
              fieldChar.remove();
              System.out.println("getFieldType -> " + fieldChar.getFieldType());
              System.out.println(node1.getText());
            }
          }
        }
      }
    }
    document.updateFields();
    document.save("C:\\Users\\thh\\Desktop\\你是谁.docx");
  }

  @Test
  public void test17() throws Exception {
    Document document = new Document("D:\\software\\2017311920_杨婧_学位论文_提交版本 (25).docx");
   /* NodeCollection<Footnote> nodes = document.getFirstSection()
        .getChildNodes(NodeType.FOOTNOTE, true);
    for (Footnote node : nodes) {
      for (Paragraph paragraph : node.getParagraphs()) {
        paragraph.getRuns().clear();
        Node[] array = paragraph.getChildNodes(NodeType.ANY, true).toArray();
        for (Node node1 : array) {
          System.out.println(node1);
          if (node1 instanceof FieldChar fieldChar) {
            if (fieldChar.getFieldType() == FieldType.FIELD_HYPERLINK) {
              fieldChar.remove();
              System.out.println("getFieldType -> " + fieldChar.getFieldType());
              System.out.println(node1.getText());
            }
          }
        }
      }
    }*/
    document.updateFields();
    document.save("C:\\Users\\thh\\Desktop\\你是谁.docx");
  }

  @Test
  public void test18() throws Exception {
    Document document = new Document("src/main/resources/docx/templates/test.docx");
    DocumentBuilder documentBuilder = new DocumentBuilder(document);
    documentBuilder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
    documentBuilder.moveToDocumentStart();
    documentBuilder.insertDocument(new Document("src/main/resources/docx/templates/insert.docx"),
        ImportFormatMode.KEEP_SOURCE_FORMATTING);
    documentBuilder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
    document.save("C:\\Users\\thh\\Desktop\\0226.docx");
  }

  @Test
  public void test19() throws Exception {
    Document document = new Document("src/main/resources/docx/templates/demo.docx");
    ParagraphCollection paragraphs = document.getFirstSection().getBody().getParagraphs();
    DocumentBuilder documentBuilder = new DocumentBuilder(document);
    Paragraph paragraph = paragraphs.get(1);
    //paragraph.getRuns().add(new Run(document));
    System.out.println(paragraph);
    System.out.println(paragraph.getText());
    System.out.println("--------");
    documentBuilder.moveTo(paragraph.getFirstChild());
    paragraph.getParagraphFormat().clearFormatting();
    documentBuilder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
    documentBuilder.getCurrentParagraph().getParagraphFormat()
        .setStyleIdentifier(StyleIdentifier.HEADING_1);
    System.out.println(documentBuilder.getCurrentParagraph());
    System.out.println(paragraph.getText());
    System.out.println(documentBuilder.getCurrentParagraph().getText());
    //documentBuilder.getCurrentSection().getBody().getParagraphs().insert(0,paragraph);
    // documentBuilder.insertBreak(BreakType.PARAGRAPH_BREAK);
    document.save("src/main/resources/docx/templates/result.docx");
  }

  @Test
  public void test20() throws Exception {
    Document document = new Document("src/main/resources/docx/templates/demo.docx");
    ParagraphCollection paragraphs = document.getFirstSection().getBody().getParagraphs();
// Get paragraph
    Paragraph paragraph = paragraphs.get(1);
// Set style
    paragraph.getParagraphFormat().setStyleIdentifier(StyleIdentifier.HEADING_1);
// Copy content before the paragraph into a separate section and put it before current section.
    Section currentSection = (Section) paragraph.getAncestor(NodeType.SECTION);
    Section sect = (Section) currentSection.deepClone(false);
    sect.ensureMinimum();
    currentSection.getParentNode().insertBefore(sect, currentSection);
    while (currentSection.getBody().getFirstChild() != paragraph) {
      sect.getBody().appendChild(currentSection.getBody().getFirstChild());
    }
    document.save("src/main/resources/docx/templates/result.docx");
  }

  @Test
  public void test21() throws Exception {
    Document document = new Document("src/main/resources/docx/templates/demo.docx");
    ParagraphCollection paragraphs = document.getFirstSection().getBody().getParagraphs();
// Get paragraph
    Paragraph paragraph = paragraphs.get(1);
// Set style
    paragraph.getParagraphFormat().setStyleIdentifier(StyleIdentifier.HEADING_1);
// Copy content before the paragraph into a separate section and put it before current section.
    Section currentSection = (Section) paragraph.getAncestor(NodeType.SECTION);
    Section sect = (Section) currentSection.deepClone(false);
    sect.ensureMinimum();
    currentSection.getParentNode().insertBefore(sect, currentSection);
    while (currentSection.getBody().getFirstChild() != paragraph) {
      sect.getBody().appendChild(currentSection.getBody().getFirstChild());
    }
    document.save("src/main/resources/docx/templates/result.docx");
  }

  @Test
  public void test22() throws Exception {
    Document document = new Document("C:\\Users\\thh\\Desktop\\参考文献-测试.docx");
    ObjectMapper objectMapper = new ObjectMapper();
    List<StyleConfigDto> styleConfigDtos = objectMapper.readValue(
        new File("src/main/resources/static/config.json"),
        new TypeReference<>() {
        });
    FormattingProcessShareVar formattingProcessShareVar = new FormattingProcessShareVar(
        styleConfigDtos, document);
    ReferencesFormatter referencesFormatter = new ReferencesFormatter(formattingProcessShareVar);
    ParagraphCollection paragraphs = document.getFirstSection().getBody().getParagraphs();
    for (Paragraph paragraph : paragraphs) {
      referencesFormatter.confirmTitle(paragraph);
    }
    referencesFormatter.formatTitle();
    referencesFormatter.formatBody();
    // document.updatePageLayout();
    document.save("src/main/resources/docx/templates/result.docx", SaveFormat.DOCX);
  }

  @Test
  public void test23() throws Exception {
    Document document = new Document("C:\\Users\\thh\\Desktop\\参考文献-测试.docx");
    int i = 0;
    for (Paragraph paragraph : document.getFirstSection().getBody().getParagraphs()) {
      if (i++ > 0) {
        String text = paragraph.toString(SaveFormat.TEXT).trim();
        System.out.println(text);
        for (Field field : paragraph.getRange().getFields()) {
          field.unlink();
        }
        if (!text.isBlank()) {
          paragraph.getRuns().clear();
          paragraph.getRuns().add(new Run(paragraph.getDocument(), "test text"));
        }
      }
    }
    document.save("src/main/resources/docx/templates/result.docx", SaveFormat.DOCX);
  }

  @Test
  public void test24() throws Exception {
    Document document = new Document("src/main/resources/docx/templates/dir.docx");
    Style style = document.getStyles().getByStyleIdentifier(StyleIdentifier.TOC_2);
    ParagraphFormat paragraphFormat = style.getParagraphFormat();
    TabStopCollection tabStops = paragraphFormat.getTabStops();
    tabStops.clear();
    tabStops.add(
        new TabStop(ConvertUtil.millimeterToPoint(150), TabAlignment.RIGHT, TabLeader.DOTS));
    Font font = paragraphFormat.getStyle().getFont();
    font.clearFormatting();
    font.setNameFarEast("宋体");
    font.setNameAscii("Times New Roman");
    font.setNameBi("Times New Roman");
    Font font1 = style.getFont();
    font1.clearFormatting();
    font1.setNameFarEast("宋体");
    font1.setNameAscii("Times New Roman");
    font1.setNameBi("Times New Roman");
    Section section = new Section(document);
    document.getSections().add(section);
    Body body = new Body(document);
    section.appendChild(body);
    DocumentBuilder documentBuilder = new DocumentBuilder(document);
    Paragraph paragraph = new Paragraph(document);
    body.getParagraphs().add(paragraph);
    documentBuilder.moveTo(paragraph);
    documentBuilder.insertTableOfContents("\\o \"1-3\" \\h \\z \\u");

    for (Section s : document.getSections()) {
      for (Field field : s.getBody().getRange().getFields()) {
        field.update();
      }
    }
    document.save("src/main/resources/docx/templates/result.docx", SaveFormat.DOCX);
  }

  @Data
  public class TextLanguageGroup {

    private final List<Character> chars = new ArrayList<>();
    private boolean isZh = false;
  }
}
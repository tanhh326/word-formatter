package com.crane.wordformat.formatter.global;

import com.aspose.words.CompositeNode;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.Row;
import com.aspose.words.SaveFormat;
import com.aspose.words.Section;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public class CoverHandler {

  private final Document document;
  private final List<Section> coverSections;

  protected CoverHandler(Document document, List<Section> coverSections) {
    this.document = document;
    this.coverSections = coverSections;
  }

  public Section mergeSections(List<Section> coverSections) throws Exception {
    if (coverSections.size() == 1) {
      return coverSections.get(0);
    }
    Document doc = new Document();
    Section mergedSection = new Section(doc);

    for (Section section : coverSections) {
      mergedSection.appendContent(section);
    }
    return mergedSection;
  }

  public Document executeEn(Section section) throws Exception {
    Document templateDocument = new Document(
        CoverHandler.class.getResourceAsStream("/docx/templates/cover-en.docx"));
    DocumentBuilder documentBuilder = new DocumentBuilder(templateDocument);
    documentBuilder.moveToMergeField("Title");
    documentBuilder.write(getTitleEn(section).trim());
    documentBuilder.moveToMergeField("University");
    documentBuilder.write(getUniversityEn(section).trim());
    documentBuilder.moveToMergeField("Degree");
    documentBuilder.write(getDegreeEn(section).trim());
    documentBuilder.moveToMergeField("Discipline");
    documentBuilder.write(getDisciplineEn(section).trim());
    documentBuilder.moveToMergeField("Student");
    documentBuilder.write(getStudentEn(section).trim());
    documentBuilder.moveToMergeField("Teacher");
    documentBuilder.write(getTeacherEn(section).trim());
    documentBuilder.moveToMergeField("Date");
    documentBuilder.write(getDateEn(section).trim());
    return templateDocument;
  }

  private String getTeacherEn(Section section) throws Exception {
    String regex = "\\s+[tT]hesis\\s+[Ss]upervisor\\s+:\\s*(.*)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(section.toString(SaveFormat.TEXT));
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }

  private String getDateEn(Section section) throws Exception {
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    for (Paragraph paragraph : paragraphs) {
      if (paragraph.toString(SaveFormat.TEXT)
          .matches(
              "\\s*(January|February|March|April|May|June|July|August|September|October|November|December).*\\d{4}\\s*")) {
        return paragraph.getText();
      }
    }
    return "";
  }

  private String getStudentEn(Section section) throws Exception {
    Pattern pattern = Pattern.compile(
        "\\s+[Bb]y\\s+(.*?)\r+");
    Matcher matcher = pattern.matcher(section.toString(SaveFormat.TEXT));
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }

  private String getDisciplineEn(Section section) throws Exception {
    Pattern pattern = Pattern.compile(
        "\\s+[Ii]n\\s+(.*?)\\s+[Bb]y\\s+");
    Matcher matcher = pattern.matcher(section.toString(SaveFormat.TEXT));
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }

  private String getDegreeEn(Section section) throws Exception {
    Pattern pattern = Pattern.compile(
        "[Dd]egree\\s+[Oo]f\\s+(.*?)\\s+[Ii]n\\s+");
    Matcher matcher = pattern.matcher(section.toString(SaveFormat.TEXT));
    if (matcher.find()) {
      return matcher.group(1);
    }

    pattern = Pattern.compile(
        "[Dd]egree\\s+[Oo]f\\s+(.*?)\\s+[Bb]y\\s+");
    matcher = pattern.matcher(section.toString(SaveFormat.TEXT));
    if (matcher.find()) {
      return matcher.group(1);
    }

    return "";
  }

  private String getUniversityEn(Section section) throws Exception {
    Pattern pattern = Pattern.compile(
        "[Ss]ubmitted\\s+[Tt]o\\s+(.*?)\\s+[Ii]n\\s+[Pp]artial\\s+[Ff]ulfillment");
    Matcher matcher = pattern.matcher(section.toString(SaveFormat.TEXT));
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }

  private String getTitleEn(Section section) throws Exception {
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    for (Paragraph paragraph : paragraphs) {
      String paragraphText = paragraph.toString(SaveFormat.TEXT).trim();
      if (paragraphText.matches("^[a-zA-Z\\s]+.*[a-zA-Z]+$")) {
        if (paragraph.getNextSibling() instanceof Paragraph nextParagraph) {
          String nextParagraphText = nextParagraph.toString(SaveFormat.TEXT).trim();
          if (StringUtils.hasText(nextParagraphText)) {
            nextParagraph.remove();
            return paragraphText + nextParagraphText;
          }
        }
        return paragraphText;
      }
    }
    return "";
  }

  private String getChineseCharacters(String str) {
    StringBuilder chineseCharacters = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN && !Character.isWhitespace(
          c)) {
        chineseCharacters.append(c);
      }
    }
    return chineseCharacters.toString();
  }

  private String[] formatNames(String studentName, String teacherName) {

    if (studentName.length() == 2 && teacherName.length() == 3) {

      studentName = studentName.charAt(0) + "\u3000\u3000\u3000" + studentName.charAt(1);
      teacherName =
          teacherName.charAt(0) + "\u3000" + teacherName.charAt(1) + "\u3000" + teacherName.charAt(
              2);

    } else if (studentName.length() == 3 && teacherName.length() == 2) {

      teacherName = teacherName.charAt(0) + "\u3000\u3000\u3000" + teacherName.charAt(1);
      studentName =
          studentName.charAt(0) + "\u3000" + studentName.charAt(1) + "\u3000" + studentName.charAt(
              2);

    } else if (studentName.length() == 3 && teacherName.length() == 3) {

      studentName =
          studentName.charAt(0) + "\u3000" + studentName.charAt(1) + "\u3000" + studentName.charAt(
              2);
      teacherName =
          teacherName.charAt(0) + "\u3000" + teacherName.charAt(1) + "\u3000" + teacherName.charAt(
              2);

    } else if (studentName.length() == 2 && teacherName.length() == 2) {
      studentName = studentName.charAt(0) + "\u3000" + studentName.charAt(1);
      teacherName = teacherName.charAt(0) + "\u3000" + teacherName.charAt(1);
    }

    return new String[]{studentName, teacherName};
  }

  private Document executeZh(Section section)
      throws Exception {
    Document templateDocument = new Document(
        CoverHandler.class.getResourceAsStream("/docx/templates/cover.docx"));
    DocumentBuilder documentBuilder = new DocumentBuilder(templateDocument);
    documentBuilder.moveToMergeField("Title");
    documentBuilder.write(getTitle(section).trim());
    documentBuilder.moveToMergeField("SubTitle");
    documentBuilder.write(getSubTitle(section).trim());
    documentBuilder.moveToMergeField("Row1Cell2");
    documentBuilder.write(getR1(section));
    documentBuilder.moveToMergeField("Row2Cell2");
    documentBuilder.write(getR2(section));
    documentBuilder.moveToMergeField("Row3Cell2");

    String student_name = getR3(section);
    String teacher_name_and_title = getR4(section);
    String[] parts = teacher_name_and_title.split("\u3000", 2);
    if (parts.length > 1) {
      String teacher_name = parts[0];
      //获取不包含空白符的名字
      String filtered_student_name = getChineseCharacters(student_name);
      String filtered_teacher_name = getChineseCharacters(teacher_name);
      //获取格式化后的名字
      String[] name_list = formatNames(filtered_student_name, filtered_teacher_name);
      //写学生名字
      documentBuilder.write(name_list[0]);
      documentBuilder.moveToMergeField("Row4Cell2");
      String title = parts[1];
      //写老师名字和头衔
      documentBuilder.write(name_list[1] + "\u3000" + title);
    }
    documentBuilder.moveToMergeField("Date");
    documentBuilder.write(getDate(section));
    return templateDocument;
  }

  public String getTitle(Section section) throws Exception {
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    for (Paragraph paragraph : paragraphs) {
      String paragraphText = paragraph.toString(SaveFormat.TEXT).trim();
      if (StringUtils.hasText(paragraphText)) {
        if (paragraph.getNextSibling() instanceof Paragraph nextParagraph) {
          String nextParagraphText = nextParagraph.toString(SaveFormat.TEXT).trim();
          if (StringUtils.hasText(nextParagraphText) && !nextParagraphText.matches("^[\\(（]+.*$")) {
            nextParagraph.remove();
            return paragraphText + nextParagraphText;
          }
        }
        return paragraphText;
      }
    }
    return "";
  }

  public String getSubTitle(Section section) {
    int flag = 0;
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    for (Paragraph paragraph : paragraphs) {
      if (StringUtils.hasText(paragraph.getText().trim())) {
        flag++;
      }
      if (flag == 2) {
        return paragraph.getText();
      }
    }
    return "";
  }

  public String getDate(Section section) {
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    for (Paragraph paragraph : paragraphs) {
      if (paragraph.getText().trim()
          .matches("\\s*[〇一二三四五六七八九十]{2,4}年[〇一二三四五六七八九十]{1,2}月\\s*")) {
        return paragraph.getText();
      }
    }
    return "";
  }

  public String getR1(Section section) {
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    String apply_name = "";
    for (Paragraph paragraph : paragraphs) {
      if (paragraph.getText().trim().matches("^\\s*培\\s*养\\s*单\\s*位[:：]*.*$")) {
        CompositeNode cn = paragraph.getAncestor(Row.class);
        if (cn != null && cn instanceof Row row) {
          apply_name = row.getLastCell().getText();
        } else {
          String[] split = paragraph.getText().split("^\\s*培\\s*养\\s*单\\s*位[:：]*");
          apply_name = split[1];
        }
      }
    }
    apply_name = apply_name.replace(":", "：").replace("：", "").trim();
    return apply_name;
  }

  public String getR2(Section section) {
    String apply_name = "";
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    for (Paragraph paragraph : paragraphs) {
      if (paragraph.getText().trim().matches("^\\s*学\\s*科[:：]*.*$")) {
        CompositeNode cn = paragraph.getAncestor(Row.class);
        if (cn != null && cn instanceof Row row) {
          apply_name = row.getLastCell().getText();
        } else {
          String[] split = paragraph.getText().split("^\\s*学\\s*科[:：]*");
          apply_name = split[1];
        }
        break;
      }
    }
    apply_name = apply_name.replace(":", "：").replace("：", "").trim();
    return apply_name;
  }

  public String getR3(Section section) {
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    String apply_name = "";
    for (Paragraph paragraph : paragraphs) {
      String text = paragraph.getText().trim();
      if (text.matches("^\\s*研\\s*究\\s*生[:：]*.*$")) {
        CompositeNode cn = paragraph.getAncestor(Row.class);
        if (cn != null && cn instanceof Row row) {
          apply_name = row.getLastCell().getText();
        } else {
          String[] split = text.split("^\\s*研\\s*究\\s*生[:：]*");
          apply_name = split[1];
        }
        break;

      } else if (text.matches("^\\s*申\\s*请\\s*人[:：]*.*$")) {
        CompositeNode cn = paragraph.getAncestor(Row.class);
        if (cn != null && cn instanceof Row row) {
          apply_name = row.getLastCell().getText();
        } else {
          String[] split = text.split("^\\s*申\\s*请\\s*人[:：]*");
          apply_name = split[1];
        }
        break;
      }
    }
    apply_name = apply_name.replace(":", "：").replace("：", "").trim();
    return apply_name;
  }

  public String extractProfessorName(String text) {
    // 匹配副教授
    String regex = "\\s*指\\s*导\\s*教\\s*师\\s*[：:\\s\\u0007]*([^副]+?)\\s*副\\s*教\\s*授";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);

    if (matcher.find()) {
      // 返回匹配到的名字部分，保留名字中的空格
      String name = matcher.group(1).trim();
      return name + "\u3000" + "副教授";
    }
    //匹配副研究员
    regex = "\\s*指\\s*导\\s*教\\s*师\\s*[：:\\s\\u0007]*([^副]+?)\\s*副\\s*研\\s*究\\s*员";
    pattern = Pattern.compile(regex);
    matcher = pattern.matcher(text);

    if (matcher.find()) {
      // 返回匹配到的名字部分，保留名字中的空格
      String name = matcher.group(1).trim();
      return name + "\u3000" + "副研究员";
    }

    //匹配教授
    regex = "\\s*指\\s*导\\s*教\\s*师\\s*[：:\\s\\u0007]*([^教]+?)\\s*教\\s*授";
    pattern = Pattern.compile(regex);
    matcher = pattern.matcher(text);

    if (matcher.find()) {
      // 返回匹配到的名字部分，保留名字中的空格
      String name = matcher.group(1).trim();
      return name + "\u3000" + "教\u3000授";
    }
    //匹配研究员
    regex = "\\s*指\\s*导\\s*教\\s*师\\s*[：:\\s\\u0007]*([^研]+?)\\s*研\\s*究\\s*员";
    pattern = Pattern.compile(regex);
    matcher = pattern.matcher(text);

    if (matcher.find()) {
      // 返回匹配到的名字部分，保留名字中的空格
      String name = matcher.group(1).trim();
      return name + "\u3000" + "研究员";
    }

    return "";
  }

  public String getR4(Section section) {
    NodeCollection<Paragraph> paragraphs = section.getChildNodes(NodeType.PARAGRAPH, true);
    for (Paragraph paragraph : paragraphs) {
      if (paragraph.getText().trim().matches("^\\s*指\\s*导\\s*教\\s*师[:：]*.*$")) {
        CompositeNode cn = paragraph.getAncestor(Row.class);
        if (cn != null && cn instanceof Row row) {
          return extractProfessorName(row.getText());
        } else {
          return extractProfessorName(paragraph.getText());
          //String[] split = paragraph.getText().split("^\\s*指\\s*导\\s*教\\s*师[:：]*");
          //return split[1];
        }
      }
    }
    return "";
  }

  public void execute() {
    try {
      Section section = mergeSections(coverSections);
      Document zhTemp = executeZh(section);
      Document enTemp = executeEn(section);
      document.appendDocument(zhTemp, ImportFormatMode.KEEP_SOURCE_FORMATTING);
      document.appendDocument(enTemp, ImportFormatMode.KEEP_SOURCE_FORMATTING);
      document.getSections().insert(0, document.getLastSection());
      document.getSections().insert(0, document.getLastSection());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

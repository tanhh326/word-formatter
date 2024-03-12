package com.crane.wordformat.formatter.global;

import com.aspose.words.Border;
import com.aspose.words.BorderType;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.Field;
import com.aspose.words.FieldType;
import com.aspose.words.Font;
import com.aspose.words.FootnoteNumberingRule;
import com.aspose.words.FootnoteOptions;
import com.aspose.words.HeaderFooter;
import com.aspose.words.HeaderFooterType;
import com.aspose.words.LineStyle;
import com.aspose.words.NumberStyle;
import com.aspose.words.PageSetup;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.Run;
import com.aspose.words.Section;
import com.aspose.words.StyleIdentifier;
import com.crane.wordformat.formatter.AbstractFormatter;
import com.crane.wordformat.formatter.enums.SectionEnums;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostHandler {

  private final Document document;

  private final List<AbstractFormatter> sortedFormatters;

  /**
   * 原文的封面章节
   */
  private final List<Section> originCoverSections = new ArrayList<>();

  public PostHandler(Document document, List<AbstractFormatter> sortedFormatters) {
    this.document = document;
    this.sortedFormatters = sortedFormatters;
  }

  /**
   * 以下操作顺序不能变
   *
   * @throws Exception
   */
  public void execute() throws Exception {
    sortSections();
    settingFootnote();
    settingHeaderFooter();
    settingPageMargin();
    new CoverHandler(document, originCoverSections).execute();
    updatePageNumber();
  }

  /**
   * 仅更新目录页码，目录全部更新会影响原目录样式
   *
   * @throws Exception
   */
  public void updatePageNumber() throws Exception {
    //字段更新依赖：尤其是在更新目录页码时，toc.updatePageNumbers()可能依赖于文档的页面布局信息。
    // 如果在此之前没有计算或更新页面布局，尝试更新页码可能会失败。通常，在更新目录或其他引用页码的字段前，
    // 使用Document.updatePageLayout()来确保页面布局是最新的。
    // error -> Invalid document model. Operation cannot be completed.
    document.updatePageLayout();
    for (AbstractFormatter formatter : sortedFormatters) {
      if (formatter.sectionEnums() == SectionEnums.目录) {
        for (Section section : formatter.getSections()) {
          int i = 0;
          for (Paragraph paragraph : section.getBody().getParagraphs()) {
            if (i++ > 0) {
              for (Field field : paragraph.getRange().getFields()) {
                field.update();
              }
              // fixme 为什么一定要清除才生效
              for (Run run : paragraph.getRuns()) {
                run.getFont().clearFormatting();
              }
            }
          }
        }
      }
    }
  }

  /**
   * 设置脚注
   */
  public void settingFootnote() {
    FootnoteOptions footnoteOptions = document.getFootnoteOptions();
    footnoteOptions.setRestartRule(FootnoteNumberingRule.RESTART_PAGE);
    footnoteOptions.setNumberStyle(NumberStyle.NUMBER_IN_CIRCLE);
  }

  /**
   * 设置页边距和装订线
   */
  public void settingPageMargin() {
    for (AbstractFormatter formatter : sortedFormatters) {
      for (Section section : formatter.getSections()) {
        PageSetup pageSetup = section.getPageSetup();
        double margin = ConvertUtil.millimeterToPoint(30);
        pageSetup.setTopMargin(margin);
        pageSetup.setBottomMargin(margin);
        pageSetup.setLeftMargin(margin);
        pageSetup.setRightMargin(margin);
        pageSetup.setGutter(0);
      }
    }
  }

  /**
   * 将原文根据章节排序
   */
  public void sortSections() {
    // 主要章节排序
    List<Section> mainSections = new ArrayList<>();
    for (AbstractFormatter formatter : sortedFormatters) {
      mainSections.addAll(formatter.getSections());
    }
    // 封面的排序
    List<Section> allSortSections = new ArrayList<>();
    for (Section section : document.getSections()) {
      // 不在主要章节中的视为封面
      if (!mainSections.contains(section)) {
        originCoverSections.add(section);
        // allSortSections.add(section);
      }
    }
    allSortSections.addAll(mainSections);
    document.getSections().clear();
    for (Section section : allSortSections) {
      section.getPageSetup().clearFormatting();
      document.getSections().add(section);
    }
  }

  /**
   * 设置页眉页脚
   *
   * @throws Exception
   */
  public void settingHeaderFooter() throws Exception {
    boolean findZhAbstract = false;
    boolean findFirstChapter = false;
    for (AbstractFormatter formatter : sortedFormatters) {
      List<Section> sections = formatter.getSections();
      if (formatter.sectionEnums() == SectionEnums.中文摘要) {
        PageSetup pageSetup = sections.get(0).getPageSetup();
        pageSetup.setRestartPageNumbering(true);
        findZhAbstract = true;
      }
      if (formatter.sectionEnums() == SectionEnums.章节) {
        PageSetup pageSetup = sections.get(0).getPageSetup();
        findFirstChapter = true;
        pageSetup.setRestartPageNumbering(true);
      }

      if (findZhAbstract) {
        for (Section section : sections) {
          generateFooter(section);
          generateHeader(section);
          PageSetup pageSetup = section.getPageSetup();
          pageSetup.setOddAndEvenPagesHeaderFooter(false);
          pageSetup.setDifferentFirstPageHeaderFooter(true);
          pageSetup.setBorderSurroundsHeader(true);
          pageSetup.setFooterDistance(ConvertUtil.millimeterToPoint(22));
          pageSetup.setHeaderDistance(ConvertUtil.millimeterToPoint(22));
          pageSetup.setPageNumberStyle(
              findFirstChapter ? NumberStyle.DECIMAL_FULL_WIDTH : NumberStyle.UPPERCASE_ROMAN);
        }
      }
    }
  }

  /**
   * 生成页眉
   *
   * @param section
   */
  private void generateHeader(Section section) throws Exception {
    Document document = (Document) section.getDocument();
    HeaderFooter headerFirst = new HeaderFooter(document, HeaderFooterType.HEADER_FIRST);
    section.getHeadersFooters().add(headerFirst);
    Paragraph title = section.getBody().getFirstParagraph();
    Paragraph headerFooterParagraph = (Paragraph) title.deepClone(true);
    ParagraphFormat paragraphFormat = headerFooterParagraph.getParagraphFormat();
    paragraphFormat.clearFormatting();
    paragraphFormat.setStyleIdentifier(StyleIdentifier.NORMAL);
    paragraphFormat.setAlignment(ParagraphAlignment.CENTER);
    paragraphFormat.setLeftIndent(0);
    paragraphFormat.setRightIndent(0);
    paragraphFormat.setFirstLineIndent(0);
    paragraphFormat.setCharacterUnitFirstLineIndent(0);
    paragraphFormat.setCharacterUnitLeftIndent(0);
    paragraphFormat.setCharacterUnitRightIndent(0);

    // 页眉横线
    Border border = paragraphFormat.getBorders().getByBorderType(BorderType.BOTTOM);
    border.setLineStyle(LineStyle.SINGLE);
    border.setLineWidth(0.75);
    border.setColor(Color.BLACK);
    for (Run run : headerFooterParagraph.getRuns()) {
      Font font = run.getFont();
      font.setSize(10.5);
      font.setNameFarEast("宋体");
      font.setNameAscii("Times New Roman");
    }
    headerFirst.getParagraphs().add(headerFooterParagraph);
  }


  /**
   * 生成页脚
   *
   * @param section
   * @throws Exception
   */
  private void generateFooter(Section section) throws Exception {
    List<Integer> typeList = Arrays.asList(
        HeaderFooterType.FOOTER_FIRST,
        HeaderFooterType.FOOTER_PRIMARY,
        HeaderFooterType.FOOTER_EVEN
    );
    Document document = (Document) section.getDocument();
    for (Integer type : typeList) {
      HeaderFooter footerFirst = new HeaderFooter(document, type);
      section.getHeadersFooters().add(footerFirst);
      Paragraph paragraph = new Paragraph(document);
      ParagraphFormat paragraphFormat = paragraph.getParagraphFormat();
      paragraphFormat.setAlignment(ParagraphAlignment.CENTER);
      Run run = new Run(document);
      Font font = run.getFont();
      font.setSize(10.5);
      font.setName("Times New Roman");
      paragraph.appendChild(run);
      paragraph.appendField(FieldType.FIELD_PAGE, true);
      footerFirst.appendChild(paragraph);
    }
  }
}

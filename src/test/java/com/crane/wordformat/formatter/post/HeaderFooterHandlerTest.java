package com.crane.wordformat.formatter.post;

import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FootnoteNumberingRule;
import com.aspose.words.FootnoteType;
import com.aspose.words.List;
import com.aspose.words.ListLevel;
import com.aspose.words.ListTemplate;
import com.aspose.words.NumberStyle;
import org.junit.jupiter.api.Test;

class HeaderFooterHandlerTest {

  private static void addOutlineHeadingParagraphs(final DocumentBuilder builder, final List list,
      final String title) {
    builder.getParagraphFormat().clearFormatting();
    builder.writeln(title);

    for (int i = 0; i < 9; i++) {
      builder.getListFormat().setList(list);
      builder.getListFormat().setListLevelNumber(i);

      String styleName = "Heading " + (i + 1);
      builder.getParagraphFormat().setStyleName(styleName);
      builder.writeln(styleName);
    }

    builder.getListFormat().removeNumbers();
  }

  @Test
  public void test() throws Exception {
    Document doc = new Document();
    DocumentBuilder builder = new DocumentBuilder(doc);
    // Footnotes and endnotes are a way to attach a reference or a side comment to text
    // that does not interfere with the main body text's flow.
    // Inserting a footnote/endnote adds a small superscript reference symbol
    // at the main body text where we insert the footnote/endnote.
    // Each footnote/endnote also creates an entry, which consists of a symbol that matches the reference
    // symbol in the main body text. The reference text that we pass to the document builder's "InsertEndnote" method.
    // Footnote entries, by default, show up at the bottom of each page that contains
    // their reference symbols, and endnotes show up at the end of the document.
    builder.write("Text 1. ");
    builder.insertFootnote(FootnoteType.FOOTNOTE, "Footnote 1.");
    builder.write("Text 2. ");
    builder.insertFootnote(FootnoteType.FOOTNOTE, "Footnote 2.");
    builder.insertBreak(BreakType.PAGE_BREAK);
    builder.write("Text 3. ");
    builder.insertFootnote(FootnoteType.FOOTNOTE, "Footnote 3.");
    builder.write("Text 4. ");
    builder.insertFootnote(FootnoteType.FOOTNOTE, "Footnote 4.");

    builder.insertBreak(BreakType.PAGE_BREAK);

    builder.write("Text 1. ");
    builder.insertFootnote(FootnoteType.ENDNOTE, "Endnote 1.");
    builder.write("Text 2. ");
    builder.insertFootnote(FootnoteType.ENDNOTE, "Endnote 2.");
    builder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
    builder.write("Text 3. ");
    builder.insertFootnote(FootnoteType.ENDNOTE, "Endnote 3.");
    builder.write("Text 4. ");
    builder.insertFootnote(FootnoteType.ENDNOTE, "Endnote 4.");

    // By default, the reference symbol for each footnote and endnote is its index
    // among all the document's footnotes/endnotes. Each document maintains separate counts
    // for footnotes and endnotes and does not restart these counts at any point.

    // We can use the "RestartRule" property to get the document to restart
    // the footnote/endnote counts at a new page or section.
    // doc.getFootnoteOptions().setRestartRule(FootnoteNumberingRule.RESTART_PAGE);
    doc.getEndnoteOptions().setRestartRule(FootnoteNumberingRule.RESTART_PAGE);
    doc.getFootnoteOptions().setRestartRule(FootnoteNumberingRule.RESTART_PAGE);
    doc.getFootnoteOptions().setNumberStyle(NumberStyle.NUMBER_IN_CIRCLE);
    doc.getEndnoteOptions().setNumberStyle(NumberStyle.NUMBER_IN_CIRCLE);
    doc.save("InlineStory.NumberingRule.docx");
  }

  @Test
  public void test2() throws Exception {
    Document doc = new Document("InlineStory.NumberingRule.docx");

    doc.getEndnoteOptions().setRestartRule(FootnoteNumberingRule.RESTART_PAGE);
    doc.getFootnoteOptions().setRestartRule(FootnoteNumberingRule.RESTART_PAGE);
    doc.getFootnoteOptions().setNumberStyle(NumberStyle.NUMBER_IN_DASH);
    doc.getEndnoteOptions().setNumberStyle(NumberStyle.NUMBER_IN_DASH);

    doc.save("InlineStory.NumberingRule2.docx");
  }

  @Test
  public void test4() throws Exception {
    outlineHeadingTemplates();
  }

  public void outlineHeadingTemplates() throws Exception {
    // "Lists.OutlineHeadingTemplates.docx"
    Document doc = new Document();

    DocumentBuilder builder = new DocumentBuilder(doc);

    // NumberStyle.SIMP_CHIN_NUM_1;
    List list = doc.getLists().add(ListTemplate.NUMBER_DEFAULT);
    // 配置第一级别 - “第1章”
    ListLevel level1 = list.getListLevels().get(0);
    level1.setNumberFormat("第 \u0000 章 ");
    level1.setNumberStyle(NumberStyle.DECIMAL_FULL_WIDTH);
    level1.setStartAt(1);
    level1.setNumberPosition(20);
    builder.write("章节");
    builder.getParagraphFormat().setStyleName("Heading 1");
    builder.getListFormat().setList(list);
    builder.getListFormat().setListLevelNumber(0);
    builder.insertBreak(BreakType.PARAGRAPH_BREAK);
    builder.write("引言");
    builder.getParagraphFormat().setStyleName("Heading 1");
    builder.getListFormat().setList(list);
    builder.getListFormat().setListLevelNumber(0);
    doc.updateListLabels();
    /*ListLevel level2 = list.getListLevels().get(1);
    level2.setNumberFormat("①.①");
    level2.setNumberStyle(NumberStyle.LOWERCASE_ROMAN);*/

    doc.save("lists2.docx");
    // addOutlineHeadingParagraphs(builder, list, "Aspose.Words Outline - \"Article Section\"");

  }
}
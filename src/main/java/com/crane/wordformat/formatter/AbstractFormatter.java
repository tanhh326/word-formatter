package com.crane.wordformat.formatter;

import com.aspose.words.Document;
import com.aspose.words.Paragraph;
import com.aspose.words.Section;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFormatter {

  protected final FormattingProcessShareVar formattingProcessShareVar;

  protected final List<Paragraph> titles = new ArrayList<>();

  public AbstractFormatter(FormattingProcessShareVar formattingProcessShareVar) {
    this.formattingProcessShareVar = formattingProcessShareVar;
  }

  public List<Section> getSections() {
    return titles.stream().map(it -> it.getParentSection()).toList();
  }

  public abstract SectionEnums sectionEnums();

  public abstract void confirmTitle(Paragraph paragraph) throws Exception;

  /**
   * @param paragraph 段落必须来自正文当中
   * @param document
   * @throws Exception
   */
  public void findStartFlag(Paragraph paragraph, Document document) throws Exception {
    confirmTitle(paragraph);
      /*formattingProcessShareVar.setCurrentSection(sectionEnums());
      formatStartTitle(paragraph);
      DocumentBuilder documentBuilder = new DocumentBuilder(document);
      // 区分章节Section
      documentBuilder.moveTo(paragraph);
      Node previousSibling = paragraph.getPreviousSibling();
      if (previousSibling != null) {
        documentBuilder.moveTo(previousSibling);
      } else {
        System.out.println("section开头");
      }
      documentBuilder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
      documentBuilder.getCurrentParagraph().remove();*/
  }

  public abstract void formatTitle() throws Exception;

  public abstract void formatBody()
      throws Exception;

}

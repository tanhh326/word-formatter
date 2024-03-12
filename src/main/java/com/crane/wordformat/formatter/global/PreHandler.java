package com.crane.wordformat.formatter.global;

import com.aspose.words.Comment;
import com.aspose.words.ControlChar;
import com.aspose.words.Document;
import com.aspose.words.Field;
import com.aspose.words.FieldCollection;
import com.aspose.words.FieldType;
import com.aspose.words.HeaderFooter;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphCollection;
import com.aspose.words.Run;
import com.aspose.words.Section;
import com.aspose.words.SectionCollection;
import com.aspose.words.StructuredDocumentTag;
import com.crane.wordformat.formatter.utils.StyleUtils;
import java.util.ArrayList;
import java.util.List;

public class PreHandler {

  private final Document document;

  public PreHandler(Document document) {
    this.document = document;
  }

  /**
   * 合并所有分节到一个分节
   *
   * @throws Exception
   */
  private void mergeAllSectionToSingle() throws Exception {
    // 删除所有分节
    for (int i = document.getSections().getCount() - 2; i >= 0; i--) {
      // 复制到最后一个分节
      document.getLastSection().prependContent(document.getSections().get(i));
      document.getSections().get(i).remove();
    }
    for (Object childNode : document.getChildNodes(NodeType.ANY, true)) {
      if (childNode instanceof Paragraph paragraph) {
        // 清除段落中的分节符，如果不删的话，分节符会导致章节的标题有两行，并且在不同页面上
        paragraph.getRange().replace(ControlChar.SECTION_BREAK, "");
      }
    }
  }

  /**
   * 合并自动编号，因为下一步合并章节会导致自动编号丢失，所以需要提前合并
   *
   * @throws Exception
   */
  private void mergeAutoNumber() throws Exception {
    document.updateListLabels();
    SectionCollection sections = document.getSections();
    for (Section section : sections) {
      ParagraphCollection paragraphs = section.getBody().getParagraphs();
      for (Paragraph paragraph : paragraphs) {
        // 排除非标题的自动编号
        if (StyleUtils.isHeading(paragraph) && paragraph.isListItem()) {
          String labelString = paragraph.getListLabel().getLabelString();
          String text = paragraph.getText();
          // 可能会有隐藏的自动编号，需要判断下有其它文字 !text.isBlank()
          if (!labelString.isBlank()) {
            if (!text.isBlank()) {
              paragraph.getRuns()
                  .insert(0, new Run(document, labelString));
            }
          }
          paragraph.getListFormat().removeNumbers();
        }
      }
    }
  }

  /**
   * 处理文档前的清理工作
   */
  private void clearAnother() {
    NodeCollection<Node> nodes = document.getChildNodes(NodeType.ANY, true);
    List<Node> removeNodes = new ArrayList<>();
    for (Node node : nodes) {
      if (node instanceof HeaderFooter) {
        removeNodes.add(node);
      } else if (node instanceof Comment) {
        removeNodes.add(node);
      }
    }
    removeNodes.forEach(it -> it.remove());
  }

  private void clearToc() {
    NodeCollection<StructuredDocumentTag> structuredDocumentTags = document.getFirstSection()
        .getBody()
        .getChildNodes(NodeType.STRUCTURED_DOCUMENT_TAG, false);
    for (StructuredDocumentTag tag : structuredDocumentTags) {
      FieldCollection fields = tag.getRange().getFields();
      for (Field field : fields) {
        if (field.getType() == FieldType.FIELD_TOC) {
          tag.remove();
          break;
        }
      }
    }
  }

  /**
   * 以下操作顺序不能变
   *
   * @throws Exception
   */
  public void execute() throws Exception {
    clearAnother();
    mergeAutoNumber();
    mergeAllSectionToSingle();
    clearToc();
  }
}

package com.crane.wordformat.formatter.global;

import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImportFormatMode;
import com.crane.wordformat.restful.dto.FormatProcessDTO.Cover;

public class CoverHandler2 {

  private final FormattingProcessShareVar formattingProcessShareVar;

  protected CoverHandler2(FormattingProcessShareVar formattingProcessShareVar) {
    this.formattingProcessShareVar = formattingProcessShareVar;
  }

  private Document executeZh() {
    Cover zhCover = this.formattingProcessShareVar.getFormatProcessDTO().getZhCover();
    Document coverDoc = zhCover.getDocument();
    DocumentBuilder documentBuilder = new DocumentBuilder(coverDoc);
    zhCover.getForm().forEach((k, v) -> {
      try {
        documentBuilder.moveToMergeField(k);
        documentBuilder.write(v);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    return coverDoc;
  }

  private Document executeEn() {
    Cover enCover = this.formattingProcessShareVar.getFormatProcessDTO().getEnCover();
    Document coverDoc = enCover.getDocument();
    DocumentBuilder documentBuilder = new DocumentBuilder(coverDoc);
    enCover.getForm().forEach((k, v) -> {
      try {
        documentBuilder.moveToMergeField(k);
        documentBuilder.write(v);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    return coverDoc;
  }

  public void execute() throws Exception {
    Document document = this.formattingProcessShareVar.getStudetDocument();
    DocumentBuilder documentBuilder = new DocumentBuilder(document);
    documentBuilder.moveToDocumentStart();
    documentBuilder.insertDocument(this.executeZh(), ImportFormatMode.KEEP_SOURCE_FORMATTING);
    documentBuilder.insertDocument(this.executeEn(), ImportFormatMode.KEEP_SOURCE_FORMATTING);
    documentBuilder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
  }
}

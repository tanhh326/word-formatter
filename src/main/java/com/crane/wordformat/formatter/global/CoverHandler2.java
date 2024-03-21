package com.crane.wordformat.formatter.global;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImportFormatMode;
import com.crane.wordformat.restful.dto.FormatProcessDTO.Cover;
import com.crane.wordformat.restful.entity.CoverFormPO;

public class CoverHandler2 {

  private final FormattingProcessShareVar formattingProcessShareVar;

  protected CoverHandler2(FormattingProcessShareVar formattingProcessShareVar) {
    this.formattingProcessShareVar = formattingProcessShareVar;
  }

  private Document executeZh() throws Exception {
    Cover zhCover = this.formattingProcessShareVar.getFormatProcessDTO().getZhCover();
    CoverFormPO coverFormPO = zhCover.getCoverFormPO();
    Document coverDoc = new Document(coverFormPO.getCoverTemplateUrl());
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

  private Document executeEn() throws Exception {
    Cover enCover = this.formattingProcessShareVar.getFormatProcessDTO().getEnCover();
    CoverFormPO coverFormPO = enCover.getCoverFormPO();
    Document coverDoc = new Document(coverFormPO.getCoverTemplateUrl());
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
    if (true) {
      return;
    }
    Document document = this.formattingProcessShareVar.getStudetDocument();
    //fixme 两个封面加上，中文封面没有移到最前面
    document.appendDocument(this.executeZh(), ImportFormatMode.KEEP_SOURCE_FORMATTING);
    document.getSections().insert(0, document.getLastSection());
    document.updatePageLayout();
    document.appendDocument(this.executeEn(), ImportFormatMode.KEEP_SOURCE_FORMATTING);
    document.getSections().insert(0, document.getLastSection());
  }
}

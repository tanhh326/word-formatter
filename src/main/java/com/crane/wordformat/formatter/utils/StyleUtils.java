package com.crane.wordformat.formatter.utils;

import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Font;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.aspose.words.StyleIdentifier;
import com.aspose.words.Underline;
import com.crane.wordformat.formatter.dto.StyleTemplateDto;
import com.crane.wordformat.formatter.dto.StyleTemplateDto.Indent;
import com.crane.wordformat.formatter.dto.StyleTemplateDto.Spacing;
import com.crane.wordformat.formatter.enums.IndentUnitEnum;
import com.crane.wordformat.formatter.enums.SpacingUnitEnum;
import java.awt.Color;
import java.util.Arrays;
import org.springframework.util.StringUtils;

public class StyleUtils {

  public static void merge(Paragraph paragraph, StyleTemplateDto styleTemplateDto,
      int styleIdentifier) {
    ParagraphFormat paragraphFormat = paragraph.getParagraphFormat();
    merge(paragraphFormat, styleTemplateDto, styleIdentifier);
  }

  public static void merge(ParagraphFormat paragraphFormat, StyleTemplateDto styleTemplateDto) {
    merge(paragraphFormat, styleTemplateDto, null);
  }

  public static void merge(ParagraphFormat paragraphFormat, StyleTemplateDto styleTemplateDto,
      Integer styleIdentifier) {
    paragraphFormat.clearFormatting();
    if (styleIdentifier != null) {
      paragraphFormat.setStyleIdentifier(styleIdentifier);
    }
    StyleTemplateDto.ParagraphFormat newParagraphFormat = styleTemplateDto.getParagraphFormat();
    paragraphFormat.setAlignment(newParagraphFormat.getAlignment());
    Indent indent = newParagraphFormat.getIndent();
    Indent.Config indentLeft = indent.getLeft();

    Indent.Config indentFirstLine = indent.getFirstLine();
    if (indentFirstLine.getUnit() == IndentUnitEnum.CHARACTER) {
      paragraphFormat.setFirstLineIndent(0);
      paragraphFormat.setCharacterUnitFirstLineIndent(indentFirstLine.getValue());
    } else {
      paragraphFormat.setFirstLineIndent(
          indentFirstLine.getUnit().converter.apply(indentFirstLine.getValue())
      );
      paragraphFormat.setCharacterUnitFirstLineIndent(0);
    }

    // 这两会同时生效，所以要先清空
    if (indentLeft.getUnit() == IndentUnitEnum.CHARACTER) {
      paragraphFormat.setLeftIndent(0);
      paragraphFormat.setCharacterUnitLeftIndent(indentLeft.getValue());
    } else {
      paragraphFormat.setLeftIndent(
          indentLeft.getUnit().converter.apply(indentLeft.getValue())
      );
      paragraphFormat.setCharacterUnitLeftIndent(0);
    }

    Indent.Config indentRight = indent.getRight();
    if (indentRight.getUnit() == IndentUnitEnum.CHARACTER) {
      paragraphFormat.setRightIndent(0);
      paragraphFormat.setCharacterUnitRightIndent(indentRight.getValue());
    } else {
      paragraphFormat.setRightIndent(
          indentRight.getUnit().converter.apply(indentRight.getValue())
      );
      paragraphFormat.setCharacterUnitRightIndent(0);
    }

    Spacing spacing = newParagraphFormat.getSpacing();
    Spacing.Config spacingBefore = spacing.getBefore();
    if (spacingBefore.getUnit() == SpacingUnitEnum.AUTO) {
      paragraphFormat.setSpaceBeforeAuto(true);
    } else {
      paragraphFormat.setSpaceBeforeAuto(false);
      paragraphFormat.setSpaceBefore(
          spacingBefore.getUnit().converter.apply(spacingBefore.getValue())
      );
    }

    Spacing.Config spacingAfter = spacing.getAfter();
    if (spacingAfter.getUnit() == SpacingUnitEnum.AUTO) {
      paragraphFormat.setSpaceAfterAuto(true);
    } else {
      paragraphFormat.setSpaceAfterAuto(false);
      paragraphFormat.setSpaceAfter(
          spacingAfter.getUnit().converter.apply(spacingAfter.getValue())
      );
    }

    Spacing.Config line = spacing.getLine();
    paragraphFormat.setLineSpacingRule(line.getType().getValue());
    paragraphFormat.setLineSpacing(
        line.getUnit().converter.apply(line.getValue())
    );
  }

  public static void merge(Font font, StyleTemplateDto styleTemplateDto, boolean isZh) {
    font.clearFormatting();
    StyleTemplateDto.Font newFont = styleTemplateDto.getFont();
    font.setName(
        isZh ? newFont.getNameFarEast() : newFont.getNameAscii());
    font.setSize(styleTemplateDto.getFont().getSize());
    // 默认
    font.setColor(Color.BLACK);
    font.setUnderline(Underline.NONE);
    font.setBold(false);
    font.setItalic(false);
    font.setShadow(false);
    font.setHighlightColor(Color.WHITE);
  }

  public static void merge(Font font, StyleTemplateDto styleTemplateDto) {
    font.clearFormatting();
    StyleTemplateDto.Font newFont = styleTemplateDto.getFont();
    if (StringUtils.hasText(newFont.getNameAscii())) {
      font.setNameFarEast(newFont.getNameFarEast());
      font.setNameAscii(newFont.getNameAscii());
      font.setNameBi(newFont.getNameAscii());
    } else {
      font.setName(newFont.getNameFarEast());
    }
    font.setSize(newFont.getSize());
    // 默认
    font.setColor(Color.BLACK);
    font.setUnderline(Underline.NONE);
    font.setBold(false);
    font.setItalic(false);
    font.setShadow(false);
    font.setHighlightColor(Color.WHITE);
  }

  public static void merge(Font font, StyleTemplateDto styleTemplateDto, boolean isZh,
      boolean clearUnderline) {
    if (clearUnderline) {
      font.clearFormatting();
      font.setUnderline(Underline.NONE);
    }
    StyleTemplateDto.Font newFont = styleTemplateDto.getFont();
    font.setName(isZh ? newFont.getNameFarEast() : newFont.getNameAscii());
    font.setSize(font.getSize());
    // 默认
    font.setColor(Color.BLACK);
    font.setBold(false);
    font.setItalic(false);
    font.setShadow(false);
    font.setHighlightColor(Color.WHITE);
  }

  public static boolean isNotToc(Paragraph paragraph) {
    return !Arrays.asList(
        StyleIdentifier.TOC_1,
        StyleIdentifier.TOC_2,
        StyleIdentifier.TOC_3,
        StyleIdentifier.TOC_4,
        StyleIdentifier.TOC_5,
        StyleIdentifier.TOC_6,
        StyleIdentifier.TOC_7,
        StyleIdentifier.TOC_8,
        StyleIdentifier.TOC_9
    ).contains(paragraph.getParagraphFormat().getStyleIdentifier());
  }

  public static boolean isHeading(Paragraph paragraph) {
    return Arrays.asList(
        StyleIdentifier.HEADING_1,
        StyleIdentifier.HEADING_2,
        StyleIdentifier.HEADING_3,
        StyleIdentifier.HEADING_4,
        StyleIdentifier.HEADING_5,
        StyleIdentifier.HEADING_6,
        StyleIdentifier.HEADING_7,
        StyleIdentifier.HEADING_8,
        StyleIdentifier.HEADING_9
    ).contains(paragraph.getParagraphFormat().getStyleIdentifier());
  }

  public static boolean isFirstParagraph(Paragraph title) {
    return title.getParentSection().getBody().getFirstParagraph() == title;
  }

  /**
   * 当此段落不是章节的第一个段落时，插入分节符
   *
   * @param title
   */
  public static Paragraph insertSectionBreakIfNotFirst(Paragraph title) {
    if (title.getParentSection().getBody().getFirstParagraph() == title) {
      return title;
    }
    // fixme https://forum.aspose.com/t/breaktype-section-break-new-page/279743
    DocumentBuilder documentBuilder = new DocumentBuilder((Document) title.getDocument());
    documentBuilder.moveTo(title.getFirstChild());
    title.getParagraphFormat().clearFormatting();
    documentBuilder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
    return documentBuilder.getCurrentParagraph();
  }
}

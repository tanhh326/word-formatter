package com.crane.wordformat.formatter.exce;

/**
 * 关于文档排版过程中出现错误，请抛出此异常
 */
public class FormattingException extends RuntimeException {

  public FormattingException(String message) {
    super(message);
  }
}

package com.crane.wordformat.formatter.enums;

public enum DegreeEnums {
  /*学硕, 专硕,*/
  博士("/关于学位论文使用授权的说明/03-1关于学位论文使用授权的说明（博士）.doc"),
  硕士("/关于学位论文使用授权的说明/03-2关于学位论文使用授权的说明（硕士）.doc");

  private final String path;

  DegreeEnums(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}

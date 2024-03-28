package com.crane.wordformat.formatter.enums;

public enum DegreeEnums {
  /*学硕, 专硕,*/
  博士("1", "/关于学位论文使用授权的说明/03-1关于学位论文使用授权的说明（博士）.doc"),
  硕士("2", "/关于学位论文使用授权的说明/03-2关于学位论文使用授权的说明（硕士）.doc");

  private final String path;
  private final String code;

  DegreeEnums(String code, String path) {
    this.code = code;
    this.path = path;
  }

  public static DegreeEnums getByCode(String code) {
    for (DegreeEnums degreeEnums : DegreeEnums.values()) {
      if (degreeEnums.getCode().equals(code)) {
        return degreeEnums;
      }
    }
    return null;
  }

  public String getPath() {
    return path;
  }

  public String getCode() {
    return code;
  }
}

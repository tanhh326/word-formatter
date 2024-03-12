package com.crane.wordformat.restful.global;

import java.io.Serializable;
import lombok.Data;

@Data
public class RestResponse<T> implements Serializable {

  public static final int CODE_SUCCESS = 0;

  public static final int CODE_ERROR = 500;

  private static final long serialVersionUID = 1L;

  private int code;

  private String msg;

  private T data;

  private RestResponse() {
  }

  public RestResponse(int code, String msg, T data) {
    this.setCode(code);
    this.setMsg(msg);
    this.setData(data);
  }

  public static <T> RestResponse<T> ok() {
    return new RestResponse<>(CODE_SUCCESS, "操作成功", null);
  }

  public static <T> RestResponse<T> code(int code) {
    return new RestResponse<>(code, null, null);
  }

  public static <T> RestResponse<T> ok(T data) {
    return new RestResponse<>(CODE_SUCCESS, "操作成功", data);
  }

  public static <T> RestResponse<T> ok(String msg, T data) {
    return new RestResponse<>(CODE_SUCCESS, msg, data);
  }

  public static <T> RestResponse<T> error() {
    return new RestResponse<>(CODE_ERROR, "服务异常", null);
  }

  public static <T> RestResponse<T> error(String msg) {
    return new RestResponse<>(CODE_ERROR, msg, null);
  }
}

package com.crane.wordformat.restful.system.req;

import lombok.Data;

@Data
public class SysUserLogin {

  private String username;

  private String password;

  private String code;

  private String uuid;
}

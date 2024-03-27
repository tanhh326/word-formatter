package com.crane.wordformat.restful.system.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Captcha {

  private String uuid;

  private String img;
}

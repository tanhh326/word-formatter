package com.crane.wordformat.restful.socket.msg;

import com.crane.wordformat.restful.enums.FormattingTaskStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FormatTaskMsg {

  private String id;

  private String originDoc;

  /**
   * {@link FormattingTaskStatusEnum#getValue()}
   */
  private Integer status;
}

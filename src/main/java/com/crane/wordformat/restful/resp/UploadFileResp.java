package com.crane.wordformat.restful.resp;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadFileResp {

  private String path;

  private Long length;

  private LocalDateTime createdTime;
}

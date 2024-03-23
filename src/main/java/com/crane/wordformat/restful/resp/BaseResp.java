package com.crane.wordformat.restful.resp;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class BaseResp {

  private List<LocalDateTime> dateRange;
}

package com.crane.wordformat.restful.resp;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class BaseResp {

  private List<OrderItem> orders = new ArrayList<>();

  private List<LocalDateTime> dateRange;
}

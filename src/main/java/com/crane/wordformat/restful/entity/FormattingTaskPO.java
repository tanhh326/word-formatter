package com.crane.wordformat.restful.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crane.wordformat.restful.db.BasePo;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.experimental.Accessors;

@TableName(value = "formatting_task", autoResultMap = true)
@Entity
@Data
@Accessors(chain = true)
public class FormattingTaskPO extends BasePo {

  private String originDoc;

  private String resultDoc;

  private String formatConfigName;

  private String coverName;

  private Long totalTimeSpent;

  private Integer status;
}

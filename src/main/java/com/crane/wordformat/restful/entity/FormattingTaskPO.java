package com.crane.wordformat.restful.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.crane.wordformat.restful.db.BasePo;
import com.crane.wordformat.restful.db.JpaMapJsonConverter;
import com.crane.wordformat.restful.enums.FormattingTaskStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import java.util.List;
import java.util.Map;
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

  private Double resultDocSize;

  private Long totalTimeSpent;

  /**
   * {@link FormattingTaskStatusEnum#getValue()}
   */
  private Integer status;

  @Column(columnDefinition = "longtext")
  private String errorMsg;

  @TableField(typeHandler = JacksonTypeHandler.class)
  @Column(columnDefinition = "json")
  @Convert(converter = JpaMapJsonConverter.class)
  private Map<String, Object> requestParams;
}

package com.crane.wordformat.restful.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.crane.wordformat.restful.db.BasePo;
import com.crane.wordformat.restful.db.JpaMapJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

/**
 * 封面表单
 */
@TableName(value = "cover_form", autoResultMap = true)
@Entity
@Data
@Accessors(chain = true)
@FieldNameConstants
public class CoverFormPO extends BasePo {

  private String name;

  @TableField(typeHandler = JacksonTypeHandler.class)
  @Column(columnDefinition = "json")
  @Convert(converter = JpaMapJsonConverter.class)
  private List<String> coverPreviewUrl;

  private String coverTemplateUrl;

  @TableField(typeHandler = JacksonTypeHandler.class)
  @Column(columnDefinition = "json")
  @Convert(converter = JpaMapJsonConverter.class)
  private List<Form> form;

  private String language;

  private String degree;

  @Data
  public static class Form {

    private String label;

    private String key;
  }
}

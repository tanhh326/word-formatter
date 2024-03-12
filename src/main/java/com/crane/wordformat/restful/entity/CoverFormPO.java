package com.crane.wordformat.restful.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.crane.wordformat.restful.db.BasePo;
import com.crane.wordformat.restful.db.JpaMapJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import java.util.HashMap;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 封面表单
 */
@TableName(value = "cover_form", autoResultMap = true)
@Entity
@Data
@Accessors(chain = true)
public class CoverFormPO extends BasePo {

  private String name;

  private String coverPreviewUrl;

  private String coverTemplateUrl;

  @TableField(typeHandler = JacksonTypeHandler.class)
  @Column(columnDefinition = "json")
  @Convert(converter = JpaMapJsonConverter.class)
  private HashMap<String, Object> form;
}

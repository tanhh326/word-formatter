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
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 格式化配置
 */
@TableName(value = "format_config", autoResultMap = true)
@Entity
@Data
@Accessors(chain = true)
public class FormatConfigPO extends BasePo {

    private String name;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Column(columnDefinition = "json")
    @Convert(converter = JpaMapJsonConverter.class)
    private List<Map<String, Object>> config;
}

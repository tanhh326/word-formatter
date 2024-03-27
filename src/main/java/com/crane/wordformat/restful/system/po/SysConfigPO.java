package com.crane.wordformat.restful.system.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crane.wordformat.restful.db.BasePo;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

@TableName(value = "sys_config", autoResultMap = true)
@Entity
@Data
@Accessors(chain = true)
@FieldNameConstants
public class SysConfigPO extends BasePo {

  private String name;

  private String logo;

  private String bigLogo;

  private String favicon;
}
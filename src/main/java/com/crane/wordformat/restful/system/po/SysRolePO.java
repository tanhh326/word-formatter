package com.crane.wordformat.restful.system.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crane.wordformat.restful.db.BasePo;
import jakarta.persistence.Entity;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

@TableName(value = "sys_role", autoResultMap = true)
@Entity
@Data
@Accessors(chain = true)
@FieldNameConstants
public class SysRolePO extends BasePo {

  private String name;

  private List<String> menus;

  private List<String> actions;
}

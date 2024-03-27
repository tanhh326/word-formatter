package com.crane.wordformat.restful.system.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crane.wordformat.restful.db.BasePo;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

@TableName(value = "sys_user", autoResultMap = true)
@Entity
@Data
@Accessors(chain = true)
@FieldNameConstants
public class SysUserPO extends BasePo {

  private String username;

  private String password;

  private String nickname;

  private String email;

  private String phone;

  private String avatar;

  private Integer status;
}

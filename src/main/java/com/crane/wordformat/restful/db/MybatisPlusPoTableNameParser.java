package com.crane.wordformat.restful.db;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;

public class MybatisPlusPoTableNameParser implements TableNameHandler {

  @Override
  public String dynamicTableName(String sql, String tableName) {
    return StrUtil.toUnderlineCase(tableName.substring(0, tableName.length() - 2));
  }
}
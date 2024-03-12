package com.crane.wordformat.restful.db;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class JpaPhysicalNamingStrategy implements PhysicalNamingStrategy {

  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @SneakyThrows
  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    if (!name.getText().endsWith("PO")) {
      throw new Exception("数据库实体类名必须以PO结尾");
    }
    String tableName = StrUtil.toUnderlineCase(
        name.getText().substring(0, name.getText().length() - 2));
    return new Identifier(tableName, name.isQuoted());
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return new Identifier(StrUtil.toUnderlineCase(name.getText()), name.isQuoted());
  }
}

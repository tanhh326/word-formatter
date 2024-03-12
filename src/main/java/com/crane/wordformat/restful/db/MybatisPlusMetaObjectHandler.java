package com.crane.wordformat.restful.db;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * todo user信息
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    this.strictInsertFill(metaObject, "createdTime", () -> LocalDateTime.now(),
        LocalDateTime.class);
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    this.strictUpdateFill(metaObject, "updatedTime", () -> LocalDateTime.now(),
        LocalDateTime.class);
  }
}
package com.chaowen.springboottemplate.base.common;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SqlInitializer {


  @Autowired
  private SqlValidator sqlValidator;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void initSql(@NotNull String schema) {
    try {
      //if (!sqlValidator.isSchemaSafe(schema)) {
      //  throw new RuntimeException("schema sql is not safe");
      //}

      for (String sql : schema.split(";")) {
        if (!sql.trim().isEmpty()) {
          jdbcTemplate.execute(sql.trim());
        }
        //log.debug("executed init sql: " + sql);
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "failed to initialize schema: " + e.getMessage(), e);
    }
  }


}

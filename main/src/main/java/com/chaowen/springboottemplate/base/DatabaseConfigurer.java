package com.chaowen.springboottemplate.base;

import static com.chaowen.springboottemplate.App.APP_SCAN_PACKAGE;
import static com.chaowen.springboottemplate.base.BeforeBeanInitializer.SpringEnvWrapper.getTableNameMapper;

import com.chaowen.springboottemplate.base.AfterBeanInitializer.AfterBeanInitHook;
import com.chaowen.springboottemplate.base.common.DatabaseSqls.DatabaseSqlMapper;
import com.chaowen.springboottemplate.base.common.DatabaseSqls.Index;
import com.chaowen.springboottemplate.base.common.Functions.Consumer;
import com.chaowen.springboottemplate.base.common.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.ibatis.annotations.Mapper;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StreamUtils;

public class DatabaseConfigurer {

  @Slf4j
  public static class DatabaseTableInitHook implements AfterBeanInitHook {

    @Autowired
    DbProperties dbProperties;

    @Autowired
    DatabaseSqlMapper databaseSqlMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @SneakyThrows
    public String renderSchema(
        String template, Consumer<Map<String, Object>> propsProvider) {
      Properties props = new Properties();
      // configure velocity
      props.setProperty("resource.loader", "string");
      props.setProperty("string.resource.loader.class",
          "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
      var velocityEngine = new VelocityEngine(props);
      velocityEngine.init();
      var customeProps = new HashMap<String, Object>();
      propsProvider.accept(customeProps);

      // set context variables
      var context = new VelocityContext();
      customeProps.forEach(context::put);

      // render the template
      var writer = new StringWriter();
      velocityEngine.evaluate(context, writer, "render_sql", template);

      // output the rendered SQL
      return writer.toString();
    }

    @Override
    @SneakyThrows
    public void run() {
      // create database
      databaseSqlMapper.createDatabaseIfNotExists(dbProperties.getName());

      // create tables
      {
        var schemaTemplate = StreamUtils.copyToString(
            this.getClass().getClassLoader()
                .getResourceAsStream("schema.sql.vm"),
            StandardCharsets.UTF_8);
        // preprocess schema before init
        var schema = renderSchema(schemaTemplate, (props) -> {
          props.put("tableNameHelper", getTableNameMapper());
        });

        try {
          // you can do some custom validations before run the script here

          for (String sql : schema.split(";")) {
            if (!sql.trim().isEmpty()) {
              jdbcTemplate.execute(sql.trim());
              log.debug("Create table sql executed: " + sql);
            }
          }
        } catch (Exception e) {
          throw new RuntimeException(
              "failed to initialize schema: " + e.getMessage(), e);
        }
      }

      // create indexes
      {
        var idxList = new ArrayList<Index>();
        {
          Index index = new Index();
          index.setTableName("settings");
          index.addColumn("key");
          idxList.add(index);
        }
        {
          // add more indexes here...
        }

        // do create indexes
        {
          idxList.forEach(index -> {
            var r = Utils.trycatch(
                    () -> databaseSqlMapper.showIndexes(index.getTableName()))
                .ifFailed(it -> {
                  log.warn("create index failed, table not exist: {}", index);
                }).throwIfEx();

            var indexInfos = r.getValue();
            var keyNames = indexInfos.stream().filter(
                    it -> it.containsKey("Key_name") &&
                          Objects.equals(it.get("Key_name"), index.getName()))
                .collect(Collectors.toList());
            if (keyNames.isEmpty()) {
              {
                var cols = index.getOrderedColumns();

                cols = cols.stream().map(it -> {
                  if (!it.startsWith("`")) {
                    return Utils.fmt("{}{}{}", '`', it, '`');
                  }
                  return it;
                }).collect(Collectors.toList());

                // convert List to comma-separated string
                var columns = String.join(", ", cols);

                // pass the parameters to the MyBatis mapper
                databaseSqlMapper.createIndex(index.getName(),
                    index.getTableName(), columns);
              }
              log.debug("Table index created: {}", index);
            } else {
              log.debug("Table index exists: {}", index);
            }
          });
        }
      }

    }
  }

  @MapperScan(basePackages = {APP_SCAN_PACKAGE},
      annotationClass = Mapper.class)
  @Configuration
  @ConditionalOnDbEnabled
  public static class DatasourceConfigurer {

    @Bean
    public DatabaseTableInitHook databaseTableInitHook() {
      return new DatabaseTableInitHook();
    }

    // mysql datasource

    @Bean
    public DataSource dataSource(DbProperties dbProperties) {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbProperties.getUrl());
      config.setUsername(dbProperties.getUser());
      config.setPassword(dbProperties.getPassword());
      config.setDriverClassName("com.mysql.cj.jdbc.Driver");

      // hikari equivalent settings
      config.setMaximumPoolSize(10); // optional: default is 10
      config.setMinimumIdle(2);      // optional
      config.setConnectionTimeout(30000); // default is 30s
      config.setIdleTimeout(600000);     // default is 10min
      config.setMaxLifetime(1800000);    // default is 30min
      config.setInitializationFailTimeout(5000); // fail fast on init

      return new HikariDataSource(config);
    }
  }
}

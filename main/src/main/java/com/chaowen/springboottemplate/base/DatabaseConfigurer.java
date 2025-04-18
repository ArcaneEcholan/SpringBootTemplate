package com.chaowen.springboottemplate.base;

import static com.chaowen.springboottemplate.Application.APP_SCAN_PACKAGE;
import static com.chaowen.springboottemplate.base.BeforeBeanInitializer.SpringEnvWrapper.getTableNameMapper;

import cn.hutool.core.io.IoUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.chaowen.springboottemplate.base.AfterBeanInitializer.AfterBeanInitHook;
import com.chaowen.springboottemplate.base.common.DatabaseSqls.DatabaseSqlMapper;
import com.chaowen.springboottemplate.base.common.DatabaseSqls.Index;
import com.chaowen.springboottemplate.base.common.Functions.Consumer;
import com.chaowen.springboottemplate.base.common.Utils;
import java.io.StringWriter;
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
    public void run() {
      // create database
      databaseSqlMapper.createDatabaseIfNotExists(dbProperties.getName());

      // create tables
      {
        var schemaTemplate = IoUtil.readUtf8(this.getClass().getClassLoader()
            .getResourceAsStream("sql/schema.sql.vm"));
        // preprocess schema before init
        var schema = renderSchema(schemaTemplate, (props) -> {
          props.put("tableNameHelper", getTableNameMapper());
        });

        try {
          // you can do some custom validations before run the script here

          for (String sql : schema.split(";")) {
            if (!sql.trim().isEmpty()) {
              jdbcTemplate.execute(sql.trim());
            }
            log.debug("executed init sql: " + sql);
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
              log.debug("index created: {}", index);
            } else {
              log.debug("index exist, skip: {}", index);
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
      DruidDataSource dataSource = new DruidDataSource();
      dataSource.setConnectionErrorRetryAttempts(5);
      dataSource.setTimeBetweenConnectErrorMillis(5000);
      dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
      dataSource.setUrl(dbProperties.getUrl());
      dataSource.setUsername(dbProperties.getUser());
      dataSource.setPassword(dbProperties.getPassword());
      return dataSource;
    }
  }
}

package com.chaowen.springboottemplate.apphooks;

import static com.chaowen.springboottemplate.base.BeforeBeanInitializer.SpringEnvWrapper.getTableNameMapper;

import cn.hutool.core.io.IoUtil;
import com.chaowen.springboottemplate.base.AfterBeanInitializer.AfterBeanInitHook;
import com.chaowen.springboottemplate.base.AfterBeanInitializer.AfterBeanInitOrder;
import com.chaowen.springboottemplate.base.common.DatabaseSqls.Index;
import com.chaowen.springboottemplate.base.common.DbIndexCreator;
import com.chaowen.springboottemplate.base.common.Functions.Consumer;
import com.chaowen.springboottemplate.base.common.SqlRenderer;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

public class AfterBeanInitHooks {

  /**
   * Control the order of hooks here
   */
  @Component
  public static class Order implements AfterBeanInitOrder {

    @Autowired
    DatabaseTableInitHook databaseTableInit;

    @Override
    public void getOrderedHooks(List<AfterBeanInitHook> hooks) {
      hooks.add(databaseTableInit);
    }
  }

  @Slf4j
  @Component
  public static class DatabaseTableInitHook implements AfterBeanInitHook {

    @Autowired
    SqlRenderer sqlRenderer;

    @Autowired
    DbIndexCreator dbIndexCreator;

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
      var schemaTemplate = IoUtil.readUtf8(this.getClass().getClassLoader()
          .getResourceAsStream("sql/schema.sql.vm"));
      // preprocess schema before init
      var schema = renderSchema(schemaTemplate, (props) -> {
        props.put("tableNameHelper", getTableNameMapper());
      });

      // execute schema init script
      {
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
          // ...
        }
        dbIndexCreator.createIdxs(idxList);
      }

    }
  }


}

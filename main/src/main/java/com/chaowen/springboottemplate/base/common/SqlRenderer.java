package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.common.Functions.Provider;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.var;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Component;

@Component
public class SqlRenderer {

  /**
   * Render sql. Eg, add prefix and suffix to sql template.
   */
  @SneakyThrows
  public String getSql(String template, Provider<Map<String, Object>> propsProvider) {
    Properties props = new Properties();
    // configure velocity
    props.setProperty("resource.loader", "string");
    props.setProperty("string.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
    var velocityEngine = new VelocityEngine(props);
    velocityEngine.init();

    var computedProps = Utils.withDefault(propsProvider.apply(), new HashMap<String, Object>());
    // set context variables
    var context = new VelocityContext();
    context.put("prefix", Utils.withDefault(computedProps.get("prefix"), ""));
    context.put("suffix", Utils.withDefault(computedProps.get("suffix"), ""));

    // render the template
    var writer = new StringWriter();
    velocityEngine.evaluate(context, writer, "render_sql", template);

    // output the rendered SQL
    return writer.toString();
  }
}

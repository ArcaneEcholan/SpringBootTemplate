package com.chaowen.springboottemplate.common.auxiliry;

import java.util.Objects;
import lombok.var;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

public class DevToolConfigurer {

  public static class DevToolEnabledCondition implements Condition {

    @Override
    public boolean matches(
        ConditionContext context, AnnotatedTypeMetadata metadata) {
      Environment env = context.getEnvironment();
      var enabledTools = env.getProperty("dev.tools.enable", Boolean.class);
      return Objects.equals(true, enabledTools);
    }
  }

  @Controller
  @Conditional(DevToolEnabledCondition.class)
  public static class WssToolController {

    private static final String WSS_TOOL_HTML = "devtools/wsstool.html";

    @GetMapping("/devtool/wss")
    public ResponseEntity<Resource> getWssTool() {
      Resource resource = new ClassPathResource(WSS_TOOL_HTML);
      if (!resource.exists()) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(resource);
    }
  }

}

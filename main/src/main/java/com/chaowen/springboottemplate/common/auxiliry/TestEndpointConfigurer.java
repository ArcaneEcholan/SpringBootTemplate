package com.chaowen.springboottemplate.common.auxiliry;

import static com.chaowen.springboottemplate.base.common.AppResponses.JsonResult.ok;
import static com.chaowen.springboottemplate.base.common.Consts.API_PREFIX;

import com.chaowen.springboottemplate.base.common.AppResponses.JsonResult;
import com.chaowen.springboottemplate.base.common.AuthLogin;
import com.chaowen.springboottemplate.base.common.Permission;
import java.util.Objects;
import lombok.var;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class TestEndpointConfigurer {

  public static class TestEndpointsEnabledCondition implements Condition {

    @Override
    public boolean matches(
        ConditionContext context, AnnotatedTypeMetadata metadata) {
      Environment env = context.getEnvironment();
      var enabledTools =
          env.getProperty("test.endpoints.enable", Boolean.class);
      return Objects.equals(true, enabledTools);
    }
  }

  @RestController
  @Validated
  @Conditional(TestEndpointsEnabledCondition.class)
  public static class TestEndpointsController {

    public static final String EMPTY_ENDPOINT_FOR_AUTH_TEST =
        "/test/empty/auth";

    public static final String EMPTY_ENDPOINT_FOR_AUTH_TEST_PERM_FREE =
        "/test/empty/auth/perm/free";

    @AuthLogin
    @Permission
    @GetMapping(API_PREFIX + EMPTY_ENDPOINT_FOR_AUTH_TEST)
    public JsonResult emptyEpForAuth() {
      return ok();
    }

    @AuthLogin
    @GetMapping(API_PREFIX + EMPTY_ENDPOINT_FOR_AUTH_TEST_PERM_FREE)
    public JsonResult emptyEpForAuthPermFree() {
      return ok();
    }
  }

}

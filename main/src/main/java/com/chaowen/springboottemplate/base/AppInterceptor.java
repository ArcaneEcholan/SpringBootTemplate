package com.chaowen.springboottemplate.base;

import com.chaowen.springboottemplate.base.common.OrderedHandlerInterceptor;
import com.chaowen.springboottemplate.mvchooks.MvcHookAround;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
@Slf4j
public class AppInterceptor implements OrderedHandlerInterceptor {

  @Autowired
  Environment environment;

  @Autowired
  MvcHookAround mvcHookAround;

  @Override
  public boolean preHandle(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response, @NotNull Object handler) {

    var uri = request.getRequestURI();
    if (uri.startsWith("/error")) {
      return true;
    }

    log.debug("> Before Mvc Request");

    // only intercept HandlerMethod
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }

    return mvcHookAround.beforeMvcRequest(request, response, handler);
  }

  @Override
  public void afterCompletion(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response, @NotNull Object handler,
      Exception ex) {
  }

  @Override
  public int getOrder() {
    return 0;
  }
}


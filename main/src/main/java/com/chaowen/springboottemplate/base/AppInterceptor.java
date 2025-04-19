package com.chaowen.springboottemplate.base;

import com.chaowen.springboottemplate.base.common.OrderedHandlerInterceptor;
import com.chaowen.springboottemplate.mvchooks.MvcHookAround;
import com.chaowen.springboottemplate.mvchooks.ThreadLocalUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.catalina.connector.ResponseFacade;
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

    log.info("{} {}", request.getMethod(), request.getRequestURI());
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

    var responseFacade = (ResponseFacade) response;
    int status = responseFacade.getStatus();
    if(status != 200 &&( responseFacade.isFinished() || responseFacade.isCommitted() ) ) {
      log.debug("High possibility to forward to /error; status: {}, finish: {}, commited: {}", status, responseFacade.isFinished(),  responseFacade.isCommitted()  ) ;
      ThreadLocalUtil.set("mvc_after_hook_delayed", true);
      return;
    }

    mvcHookAround.afterMvcRequest(request, response);
  }

  @Override
  public int getOrder() {
    return 0;
  }
}


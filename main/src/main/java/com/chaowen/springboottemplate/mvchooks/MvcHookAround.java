package com.chaowen.springboottemplate.mvchooks;

import com.chaowen.springboottemplate.base.common.AppResponses.CommonErrCodes;
import com.chaowen.springboottemplate.base.common.AppResponses.JsonResult;
import com.chaowen.springboottemplate.base.common.SimpleFactories;
import com.chaowen.springboottemplate.utils.ThreadLocalUtils;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@Component
public class MvcHookAround {

  @Autowired
  private CommonErrCodes commonErrCodes;

  public boolean beforeMvcRequest(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response, @NotNull Object handler) {
    log.debug("== pre handle request ==");
    // Only intercept HandlerMethod
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }

    var ctx = ThreadLocalUtils.getCtx();
    ctx.setStartTs(System.currentTimeMillis());

    var uri = request.getRequestURI();
    var method = ((HandlerMethod) handler).getMethod();

    log.debug("== pre handle success ==");

    return true;
  }

  public Map<Object, Object> beforeWritingBody(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response, JsonResult jsonResult) {

    log.debug("== before writting body ==");
    var r = SimpleFactories.ofMap();

    if (jsonResult.getCode() == null) {
      log.error("code is required in response");
      return SimpleFactories.ofMap("code", commonErrCodes.serverErr().getCode(),
          "msg", commonErrCodes.serverErr().getMsg());
    }
    r.put("code", jsonResult.getCode());

    if (jsonResult.getMsg() != null) {
      r.put("msg", jsonResult.getMsg());
    }

    if (jsonResult.getData() != null) {
      r.put("data", jsonResult.getData());
    }

    return r;
  }

  public void afterMvcRequest(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response) {
    log.debug("== after request ==");
    ThreadLocalUtils.clear();
  }

}

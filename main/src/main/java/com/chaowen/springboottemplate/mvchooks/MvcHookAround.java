package com.chaowen.springboottemplate.mvchooks;

import com.chaowen.springboottemplate.base.AppResponses.JsonResult;
import com.chaowen.springboottemplate.base.common.SimpleFactories;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@Component
public class MvcHookAround {

  public boolean beforeMvcRequest(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response, @NotNull Object handler) {
    var ctx = ThreadLocalUtil.getCtx();
    ctx.setStartTs(System.currentTimeMillis());

    var method = ((HandlerMethod) handler).getMethod();

    return true;
  }

  public Object beforeWritingBody(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response, boolean returnJson, Object data) {

    log.debug("> Before Writing Response Body");

    if (returnJson && data instanceof JsonResult) {
      var r = SimpleFactories.ofMap();
      var jsonResult = (JsonResult) data;
      if (jsonResult.getCode() == null) {
        log.error("code is required in response");
        return SimpleFactories.ofMap("code",
            RespCodeImpl.SERVER_ERROR.getCode(), "msg",
            RespCodeImpl.SERVER_ERROR.getMsg());
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

    return data;
  }

  public void afterMvcRequest(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response) {
    log.debug("> After Mvc Request");
    ThreadLocalUtil.clear();
  }

}

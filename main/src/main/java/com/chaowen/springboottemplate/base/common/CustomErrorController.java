package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.AppResponses.JsonResult;
import com.chaowen.springboottemplate.mvchooks.MvcHookException;
import com.chaowen.springboottemplate.mvchooks.RespCodeImpl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

  @Autowired
  MvcHookException mvcHookException;

  public static class WebCtxException extends RuntimeException {

  }

  @RequestMapping("/error")
  @ResponseBody
  @SneakyThrows
  public ResponseEntity handleApiError(
      HttpServletRequest request, HttpServletResponse response) {
    if (request instanceof HttpServletRequestWrapper) {
      var httpServletRequestWrapper = (HttpServletRequestWrapper) request;
      HttpServletRequest httpServletRequest =
          (HttpServletRequest) httpServletRequestWrapper.getRequest();
      return mvcHookException.exceptionHappened(httpServletRequest, response,
          new WebCtxException());
    }
    return ResponseEntity.ok(JsonResult.of(null, RespCodeImpl.SERVER_ERROR));
  }

}

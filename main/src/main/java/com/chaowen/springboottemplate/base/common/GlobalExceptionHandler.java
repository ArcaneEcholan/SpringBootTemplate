package com.chaowen.springboottemplate.base.common;

import static com.chaowen.springboottemplate.base.common.SimpleFactories.ofJson;

import com.chaowen.springboottemplate.mvchooks.MvcHookException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @Autowired
  MvcHookException mvcHookException;
  /**
   * all other exceptions come here
   */
  @ExceptionHandler({Exception.class})
  @ResponseBody
  public ResponseEntity handleException(
      HttpServletRequest req, HttpServletResponse resp, Exception ex) {
    return mvcHookException.exceptionHappened(req, resp, ex);
  }

}

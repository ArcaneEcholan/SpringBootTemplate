package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.AppResponses.JsonResult;
import com.chaowen.springboottemplate.mvchooks.MvcHookAround;
import com.chaowen.springboottemplate.mvchooks.MvcHookException;
import com.chaowen.springboottemplate.mvchooks.RespCodeImpl;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class ExtraErrorController implements ErrorController {

  @Autowired
  MvcHookException mvcHookException;

  @Autowired
  MvcHookAround mvcHookAround;

  @RequestMapping("/error")
  @SneakyThrows
  public ResponseEntity handleExtraError(
      HttpServletRequest request, HttpServletResponse response) {
    log.debug("> Handle Extra Exception");

    HttpServletRequest req = request;
    HttpServletResponse resp = response;

    if (request instanceof HttpServletRequestWrapper) {
      var httpServletRequestWrapper = (HttpServletRequestWrapper) request;
      req = (HttpServletRequest) httpServletRequestWrapper.getRequest();

      // Any other exception caused by Controller but not handled by
      // MvcHookException::exceptionHappened(),
      // or not caused by Controller(including validation), will be forwarded here.

      // For example, when spring throws a handler_mapping_not_found (route not found),
      // you have the right to decide what to return.

      // NOTE: None of MvcHookAround::*(*) will be involved around this handler.

      int statusCode = response.getStatus();

      if (statusCode == HttpStatus.NOT_FOUND.value()) {
        // deal with api not found
        if (req.getRequestURI().startsWith("/api")) {
          return ResponseEntity.notFound().build();
        }

        // deal with static not found (especially useful for SPA)

        // this is an example of serving next.js static pages
        var pageHtml = "404";
        try {
          var in = new ClassPathResource(
              "/static" + req.getRequestURI() + ".html").getInputStream();
          pageHtml = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (Exception e) {
          log.warn("/static/index.html read failed", e);
        }
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
        return new ResponseEntity(pageHtml, headers, HttpStatus.OK);
      }
    }

    return ResponseEntity.ok(JsonResult.of(null, RespCodeImpl.SERVER_ERROR));
  }
}

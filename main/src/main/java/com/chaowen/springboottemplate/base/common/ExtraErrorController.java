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
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
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
  @Autowired
  Environment env;
  @Autowired
  StaticLocationProperties staticLocationProperties;
  @Autowired
  ResourceLoader resourceLoader;
  @Autowired
  WebProperties webProperties;

  /**
   * handles exceptions outside business logic, such as those from the spring
   * framework (e.g., route not found) that can’t be customized through regular
   * exception handling. this includes exceptions not handled by
   * `exceptionHappened` — most commonly used for custom 404 pages.
   */
  @RequestMapping("/error")
  @SneakyThrows
  public ResponseEntity handleExtraEx(
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

      // deal with static not found (especially useful for SPA)
      if (statusCode == HttpStatus.NOT_FOUND.value()) {

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");

        String pageHtml = null;

        // the target search order matters!

        // for nextjs static page strategy(append .html to uri)
        //pageHtml = readCpResUtf8(req.getRequestURI() + ".html");
        //if (pageHtml != null) {
        //  return new ResponseEntity(pageHtml, headers, HttpStatus.OK);
        //}
        //pageHtml = readCpResUtf8("404.html");
        //if (pageHtml != null) {
        //  return new ResponseEntity(pageHtml, headers, HttpStatus.OK);
        //}

        // for vuejs static page strategy(js router vue-router handles routing)
        pageHtml = readCpResUtf8("index.html");
        if (pageHtml != null) {
          return new ResponseEntity(pageHtml, headers, HttpStatus.OK);
        }

        return new ResponseEntity(pageHtml, headers, HttpStatus.NOT_FOUND);
      }
    }

    return ResponseEntity.ok(JsonResult.of(null, RespCodeImpl.SERVER_ERROR));
  }

  @SneakyThrows
  String readCpResUtf8(String relativePath) {
    var finder = new StaticResourceFinder(
        StaticResourceFinder.getConfiguredStaticLocations(
            webProperties.getResources()), resourceLoader);
    var r = finder.findStaticResource(relativePath);
    if (r != null) {
      return StreamUtils.copyToString(r.getInputStream(),
          StandardCharsets.UTF_8);
    }

    return null;
  }
}

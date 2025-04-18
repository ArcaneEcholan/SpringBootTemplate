package com.chaowen.springboottemplate.base;

import com.chaowen.springboottemplate.base.common.JsonUtil;
import com.chaowen.springboottemplate.mvchooks.MvcHookAround;
import com.chaowen.springboottemplate.mvchooks.MvcHookException;
import com.chaowen.springboottemplate.utils.ThreadLocalUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
class RawInterceptorFilter extends OncePerRequestFilter {

  @Autowired
  MvcHookAround mvcHookAround;
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // collect headers
    {
      Map<String, String> headers = new HashMap<>();
      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String name = headerNames.nextElement();
        String value = Collections.list(request.getHeaders(name)).stream()
            .collect(Collectors.joining(","));
        headers.put(name, value);
      }
      ThreadLocalUtils.getCtx().setHeaders(headers);
    }

    // wrap request to allow body reuse
    filterChain.doFilter(new ContentCachingRequestWrapper(request),
        response); // forward to actual handler

    mvcHookAround.afterMvcRequest(request, response);

  }
}

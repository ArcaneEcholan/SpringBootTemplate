package com.chaowen.springboottemplate.base;

import com.chaowen.springboottemplate.mvchooks.MvcHookAround;
import com.chaowen.springboottemplate.mvchooks.ThreadLocalUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
class WebFilter extends OncePerRequestFilter {

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
      ThreadLocalUtil.getCtx().setHeaders(headers);
    }

    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
      @Override
      public String getRequestURI() {
        return decode(super.getRequestURI());
      }

      @Override
      public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        return pathInfo == null ? null : decode(pathInfo);
      }

      private String decode(String uri) {
        try {
          return URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    };

    // wrap request to allow body reuse
    filterChain.doFilter(new ContentCachingRequestWrapper(wrappedRequest),
        response); // forward to actual handler
  }
}

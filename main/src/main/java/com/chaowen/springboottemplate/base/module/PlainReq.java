package com.chaowen.springboottemplate.base.module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import lombok.var;
import org.jetbrains.annotations.Nullable;

public class PlainReq extends HttpServletRequestStub {

  private final String reqUri;
  private final byte[] body;
  private final int contentLength;
  private final Map<String, List<String>> headers;


  public PlainReq(
      String reqUri, byte[] body, Map<String, List<String>> headers) {
    this.reqUri = reqUri;
    this.body = body;
    this.contentLength = body.length;
    this.headers = headers;
  }

  public PlainReq(
      String reqUri, String body, Map<String, List<String>> headers) {
    this.reqUri = reqUri;
    this.body = body.getBytes(StandardCharsets.UTF_8);
    this.contentLength = this.body.length;
    this.headers = headers;
  }

  @Override
  public String getMethod() {
    return "POST";
  }

  @Override
  public String getRequestURI() {
    return reqUri;
  }

  @Override
  public String getContentType() {
    return "application/json";
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
        // No-op
      }

      @Override
      public int read() throws IOException {
        return byteArrayInputStream.read();
      }
    };
  }

  @Override
  public int getContentLength() {
    return contentLength;
  }

  @Override
  public String getCharacterEncoding() {
    return "UTF-8";
  }

  @Override
  @Nullable
  public String getHeader(String name) {
    var strings = headers.get(name);
    if (strings == null) {
      return null;
    }
    return !strings.isEmpty() ? strings.get(0) : null;
  }

  @Override
  public Enumeration<String> getHeaders(String s) {
    var strings = headers.get(s);
    if (strings == null) {
      return Collections.enumeration(new ArrayList<>());
    }
    return Collections.enumeration(
        !strings.isEmpty() ? strings : new LinkedList<>());
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(headers.keySet());
  }

  @Override
  public StringBuffer getRequestURL() {
    return new StringBuffer().append(reqUri);
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return Collections.emptyMap();
  }

}

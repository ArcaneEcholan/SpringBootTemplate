package com.chaowen.springboottemplate.base.module;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

class Stub implements HttpServletResponse {

  @Override
  public void addCookie(Cookie cookie) {

  }

  @Override
  public boolean containsHeader(String s) {
    return false;
  }

  @Override
  public String encodeURL(String s) {
    return null;
  }

  @Override
  public String encodeRedirectURL(String s) {
    return null;
  }

  @Override
  public String encodeUrl(String s) {
    return null;
  }

  @Override
  public String encodeRedirectUrl(String s) {
    return null;
  }

  @Override
  public void sendError(int i, String s) throws IOException {

  }

  @Override
  public void sendError(int i) throws IOException {

  }

  @Override
  public void sendRedirect(String s) throws IOException {

  }

  @Override
  public void setDateHeader(String s, long l) {

  }

  @Override
  public void addDateHeader(String s, long l) {

  }

  @Override
  public void setHeader(String s, String s1) {

  }

  @Override
  public void addHeader(String s, String s1) {

  }

  @Override
  public void setIntHeader(String s, int i) {

  }

  @Override
  public void addIntHeader(String s, int i) {

  }

  @Override
  public void setStatus(int i, String s) {

  }

  @Override
  public void setStatus(int i) {

  }

  @Override
  public int getStatus() {
    return 0;
  }


  @Override
  public String getHeader(String s) {
    return null;
  }

  @Override
  public Collection<String> getHeaders(String s) {
    return null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    return null;
  }

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public void setCharacterEncoding(String s) {

  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public void setContentType(String s) {

  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    return null;
  }

  @Override
  public void setContentLength(int i) {

  }

  @Override
  public void setContentLengthLong(long l) {

  }

  @Override
  public int getBufferSize() {
    return 0;
  }

  @Override
  public void setBufferSize(int i) {

  }

  @Override
  public void flushBuffer() throws IOException {

  }

  @Override
  public void resetBuffer() {

  }

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public void reset() {

  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public void setLocale(Locale locale) {

  }
}

public class PlainResp extends Stub {

  private final Map<String, List<String>> headers = new HashMap<>();
  private final ByteArrayOutputStream outputStream =
      new ByteArrayOutputStream();
  private int status = HttpServletResponse.SC_OK;

  @Override
  public int getStatus() {
    return status;
  }

  @Override
  public void setStatus(int sc) {
    this.status = sc;
  }

  @Override
  public void setHeader(String name, String value) {
    headers.put(name, Collections.singletonList(value));
  }

  @Override
  public void addHeader(String name, String value) {
    headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
  }

  @Override
  public String getHeader(String name) {
    List<String> values = headers.get(name);
    if (values == null) {
      return null;
    }
    return !values.isEmpty() ? values.get(0) : null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    return headers.keySet();
  }

  @Override
  public Collection<String> getHeaders(String name) {
    return headers.getOrDefault(name, Collections.emptyList());
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return new ServletOutputStream() {
      @Override
      public void write(int b) throws IOException {
        outputStream.write(b);
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {
        // no-op
      }
    };
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    return new PrintWriter(
        new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
  }

  public String getBody() {
    return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
  }

  // Other unimplemented methods can throw UnsupportedOperationException or return defaults
}

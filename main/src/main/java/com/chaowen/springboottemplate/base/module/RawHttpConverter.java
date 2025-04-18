package com.chaowen.springboottemplate.base.module;

import cn.hutool.core.io.IoUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.var;

public class RawHttpConverter {

  @SneakyThrows
  public static byte[] req2Raw(PlainReq plainReq) {
    String body = IoUtil.readUtf8(plainReq.getInputStream());
    if (plainReq.getRequestURI() == null || plainReq.getHeaderNames() == null ||
        body == null || body.isEmpty()) {
      throw new RuntimeException(
          "Unrecognized protocol: missing required fields");
    }

    var httpBuilder = new StringBuilder();
    httpBuilder.append("POST ").append(plainReq.getRequestURI())
        .append(" HTTP/1.1\r\n");

    // headers
    Collections.list(plainReq.getHeaderNames()).forEach((key) -> {
      var value = plainReq.getHeader(key);
      httpBuilder.append(key).append(": ").append(value).append("\r\n");
    });

    // content-length
    httpBuilder.append("Content-Length: ").append(plainReq.getContentLength())
        .append("\r\n");
    httpBuilder.append("\r\n");

    // body
    httpBuilder.append(body);

    return httpBuilder.toString().getBytes();
  }

  @SneakyThrows
  public static PlainResp raw2Resp(byte[] rawHttp) {
    try {
      String httpText = new String(rawHttp, StandardCharsets.UTF_8);
      String[] lines = httpText.split("\r\n");

      // Parse status line
      var statusLine = lines[0].split(" ");
      if (statusLine.length < 3 || !"HTTP/1.1".equals(statusLine[0])) {
        throw new RuntimeException("Unrecognized protocol");
      }
      final int status = Integer.parseInt(statusLine[1]);

      // parse headers
      Map<String, List<String>> headers = new HashMap<>();
      int i = 1;
      while (i < lines.length && !lines[i].isEmpty()) {
        String[] header = lines[i].split(": ", 2);
        if (header.length == 2) {
          headers.computeIfAbsent(header[0], k -> new ArrayList<>())
              .add(header[1]);
        }
        i++;
      }

      if (!headers.containsKey("Content-Type") ||
          headers.get("Content-Type").isEmpty()) {
        throw new RuntimeException(
            "Unrecognized protocol: missing Content-Type");
      }

      // parse body
      StringBuilder bodyBuilder = new StringBuilder();
      while (++i < lines.length) {
        bodyBuilder.append(lines[i]).append("\r\n");
      }
      byte[] body = bodyBuilder.toString().trim().getBytes();

      if (body.length == 0) {
        body = "{}".getBytes();
      }

      var plainResp = new PlainResp();
      plainResp.setStatus(status);

      headers.forEach((key, values) -> {
        values.forEach(value -> {
          plainResp.addHeader(key, value);
        });
      });
      plainResp.getOutputStream().write(body);

      return plainResp;
    } catch (Exception e) {
      throw new RuntimeException("Unrecognized protocol", e);
    }
  }

  @SneakyThrows
  public static byte[] respToRaw(PlainResp plainResp) {
    if (plainResp.getHeaderNames() == null ||
        plainResp.getOutputStream() == null) {
      throw new RuntimeException(
          "Unrecognized protocol: missing required fields");
    }

    StringBuilder httpBuilder = new StringBuilder();
    httpBuilder.append("HTTP/1.1 ").append(plainResp.getStatus())
        .append(" OK\r\n");

    // append headers
    plainResp.getHeaderNames().forEach((key) -> {
      for (String value : plainResp.getHeaders(key)) {
        httpBuilder.append(key).append(": ").append(value).append("\r\n");
      }
    });

    // append content-length
    byte[] body = plainResp.getBody().getBytes(StandardCharsets.UTF_8);
    httpBuilder.append("Content-Length: ").append(body.length).append("\r\n");
    httpBuilder.append("\r\n");

    // append body
    httpBuilder.append(new String(body));

    return httpBuilder.toString().getBytes();
  }

  public static PlainReq raw2Req(String rawHttp) {
    try {
      String[] lines = rawHttp.split("\r\n");

      // parse request line
      String[] requestLine = lines[0].split(" ");
      if (requestLine.length < 3 || !"POST".equals(requestLine[0])) {
        throw new IllegalArgumentException("Unrecognized protocol");
      }
      final String reqUri = requestLine[1];

      // parse headers
      Map<String, List<String>> headers = new HashMap<>();
      int i = 1;
      while (i < lines.length && !lines[i].isEmpty()) {
        String[] header = lines[i].split(": ", 2);
        if (header.length == 2) {
          var key = header[0];
          var value = header[1];
          var values = headers.get(header[0]);
          if (values == null) {
            values = new ArrayList<>();
            headers.put(key, values);
          }
          values.add(value);
        }
        i++;
      }

      if (!headers.containsKey("Content-Type") ||
          headers.get("Content-Type").isEmpty()) {
        throw new RuntimeException(
            "Unrecognized protocol: missing Content-Type");
      }

      // Parse body
      StringBuilder bodyBuilder = new StringBuilder();
      while (++i < lines.length) {
        bodyBuilder.append(lines[i]).append("\r\n");
      }
      byte[] body = bodyBuilder.toString().trim().getBytes();

      if (body.length == 0) {
        body = "{}".getBytes();
      }

      return new PlainReq(reqUri, body, headers);
    } catch (Exception e) {
      throw new RuntimeException("Unrecognized protocol", e);
    }
  }
}

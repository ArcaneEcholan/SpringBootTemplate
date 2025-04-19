package com.chaowen.springboottemplate.base;

import com.chaowen.springboottemplate.base.common.SimpleFactories;
import com.chaowen.springboottemplate.mvchooks.MvcHookAround;
import com.chaowen.springboottemplate.mvchooks.RespCodeImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

public class AppResponses {

  public interface RespCode {

    String getCode();

    String getMsg();
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Component
  public static class JsonResult {

    @Nullable
    private Object data;

    @NotNull
    private String code;

    @NotNull
    private String msg;

    public static JsonResult of(Object data, RespCode code) {
      JsonResult jsonResult = new JsonResult();
      jsonResult.data = data;
      jsonResult.code = code.getCode();
      jsonResult.msg = code.getMsg();
      return jsonResult;
    }

    public static JsonResult of(
        Object data, @NotNull String respCode, @NotNull String msg) {
      JsonResult jsonResult = new JsonResult();
      jsonResult.data = data;
      jsonResult.code = respCode;
      jsonResult.msg = msg;
      return jsonResult;
    }

    public static JsonResult ok() {
      return of(null, RespCodeImpl.SUCCESS);
    }

    public static JsonResult ok(Object data) {
      return of(data, RespCodeImpl.SUCCESS);
    }

  }

  @Component
  @ControllerAdvice
  public static class ResponseFormatter implements ResponseBodyAdvice<Object> {

    @Autowired
    MvcHookAround mvcHookAround;

    @Override
    public boolean supports(
        MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType) {

      var returnTypeClass = returnType.getParameterType();

      // apply this advice only to methods returning JsonResult or ResponseEntity<JsonResult>
      // ResponseEntity<StreamingResponseBody> won't get here
      return JsonResult.class.isAssignableFrom(returnTypeClass) ||
             (ResponseEntity.class.isAssignableFrom(returnTypeClass));
    }

    @Override
    public Object beforeBodyWrite(
        Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request, ServerHttpResponse response) {

      var req = ((ServletServerHttpRequest) request).getServletRequest();
      var resp = ((ServletServerHttpResponse) response).getServletResponse();
      boolean returnJson = selectedConverterType == MappingJackson2HttpMessageConverter.class;

      // process JsonResult directly
      return mvcHookAround.beforeWritingBody(req, resp, returnJson, body);
    }

  }

}

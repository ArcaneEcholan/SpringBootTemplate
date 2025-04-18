package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.auxiliry.Staticed;
import com.chaowen.springboottemplate.mvchooks.MvcHookAround;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  public interface PostResponsePreprocessor {

    void run(JsonResult jsonResult, Map<Object, Object> respBody)
        throws Exception;

    @Component
    class Dummy implements PostResponsePreprocessor {

      public void run(JsonResult jsonResult, Map<Object, Object> respBody)
          throws Exception {
      }
    }
  }

  public interface CommonErrCodes {

    RespCode serverErr();

    RespCode paramError();

    RespCode success();
  }

  public interface RespCode {

    String getCode();

    String getMsg();
  }

  public interface Resp {

    void setHeader(@NotNull String key, @NotNull String value);

    void put(String key, Object value);

    String bodyStr();
  }

  @Component
  public static class ErrorErrCodesImpl implements CommonErrCodes {

    public RespCode paramError() {
      return new RespCode() {
        @Override
        public String getCode() {
          return "FRONT_END_PARAMS_ERROR";
        }

        @Override
        public String getMsg() {
          return "参数错误";
        }
      };
    }

    public RespCode serverErr() {
      return new RespCode() {
        @Override
        public String getCode() {
          return "SERVER_ERROR";
        }

        @Override
        public String getMsg() {
          return "服务内部错误";
        }
      };
    }

    public RespCode success() {
      return new RespCode() {
        @Override
        public String getCode() {
          return "SUCCESS";
        }

        @Override
        public String getMsg() {
          return "成功";
        }
      };
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Component
  @Staticed
  public static class JsonResult {

    static CommonErrCodes commonErrCodes;

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
      return of(null, commonErrCodes.success());
    }

    public static JsonResult ok(Object data) {
      return of(data, commonErrCodes.success());
    }

  }

  @Component
  @ControllerAdvice
  public static class ResponseFormatter implements ResponseBodyAdvice<Object> {

    @Autowired
    private CommonErrCodes commonErrCodes;
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

      if (selectedConverterType != MappingJackson2HttpMessageConverter.class) {
        return body;
      }

      var req = ((ServletServerHttpRequest) request).getServletRequest();
      var resp = ((ServletServerHttpResponse) response).getServletResponse();

      // process JsonResult directly
      if (body instanceof JsonResult) {
       return  mvcHookAround.beforeWritingBody(req, resp, (JsonResult) body);
      }

      return  mvcHookAround.beforeWritingBody(req, resp, JsonResult.of(
          SimpleFactories.ofJson("detail", "please return JsonResult!!!"),
          commonErrCodes.serverErr()));
    }

  }

}

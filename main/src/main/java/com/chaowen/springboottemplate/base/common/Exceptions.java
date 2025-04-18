package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.auxiliry.Staticed;


import com.chaowen.springboottemplate.base.common.AppResponses.RespCode;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Staticed
public class Exceptions {

  public static final String PARAM_VIOLATION_KEY = "violations";
  static AppResponses.CommonErrCodes commonErrCodes;

  public static BackendException newEx(
      @Nullable Object data, @NotNull AppResponses.RespCode code) {
    return newEx0(data, code);
  }

  public static BackendException newEx(
      @NotNull String msgs, @NotNull AppResponses.RespCode code) {
    return newEx0(SimpleFactories.ofJson("error-info", msgs), code);
  }

  public static BackendException newEx(
      @NotNull AppResponses.RespCode code) {
    return newEx0(null, code);
  }

  public static BackendException newExRaw(
      @NotNull String code, String msg) {
    return newEx0(code, msg);
  }

  public static BackendException newExWithCustomMsg(
      String msg, @NotNull AppResponses.RespCode code) {
    return new BackendException(null, code.getCode(),
        msg);
  }

  public static BackendException newExServerInternalError() {
    return newEx0(null, commonErrCodes.serverErr());
  }

  public static BackendException newExServerInternalError(
      @NotNull String msgs) {
    return newEx0(SimpleFactories.ofJson("error-info", msgs),
        commonErrCodes.serverErr());
  }

  public static BackendException genParamEx(
      List<String> violations) {
    return newEx0(SimpleFactories.ofJson(PARAM_VIOLATION_KEY, violations),
        commonErrCodes.paramError());
  }

  public static BackendException genParamEx(
      String... violations) {
    return new BackendException(
        SimpleFactories.ofJson(PARAM_VIOLATION_KEY, violations),
        commonErrCodes.paramError());
  }

  static BackendException newEx0(
      @Nullable Object data, @NotNull AppResponses.RespCode code) {
    return new BackendException(data, code);
  }

  static BackendException newEx0(
      @NotNull String code, String msg) {
    return new BackendException(null, code, msg);
  }

  @Data
  public static class BackendException extends RuntimeException {

    private Object data;

    private String code;

    private String msg;

    public BackendException(Object data, RespCode centerRespCode) {
      super(centerRespCode.getCode());
      this.data = data;
      this.code = centerRespCode.getCode();
      this.msg = centerRespCode.getMsg();
    }

    public BackendException(Object data, String code, String msg) {
      super(code);
      this.data = data;
      this.code = code;
      this.msg = msg;
    }

    @Override
    public String toString() {
      return JsonUtil.toJsonString(
          SimpleFactories.ofJson("code", code, "msg", msg, "data", data));
    }
  }
}

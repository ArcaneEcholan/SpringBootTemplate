package com.chaowen.springboottemplate.mvchooks;

import com.chaowen.springboottemplate.base.AppResponses.RespCode;
import lombok.Getter;

@Getter
public enum RespCodeImpl implements RespCode {
  SUCCESS("成功"),
  SERVER_ERROR("服务内部错误"),
  PARAMS_ERROR("参数错误");


  private final String msg;

  RespCodeImpl(String msg) {
    this.msg = msg;
  }

  public String getCode() {
    return name();
  }
}

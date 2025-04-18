package com.chaowen.springboottemplate.base.module;

public interface BasicPlatformStub {

  public static final String SOCKET_PATH = "/tmp/app.socket";

  public static final String PREFIX = "/intern";

  public String call(String path, Object body, String sockPath);

  public static BasicPlatformStub make() {
    return new BasicPlatformStubImpl();
  }
}

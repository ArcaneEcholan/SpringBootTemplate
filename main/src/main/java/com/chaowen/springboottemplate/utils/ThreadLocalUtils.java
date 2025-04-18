package com.chaowen.springboottemplate.utils;


import com.chaowen.springboottemplate.common.RequestContext;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class ThreadLocalUtils {

  private static final ThreadLocal<Map<String, Object>> inner =
      new ThreadLocal<>();

  private static Map<String, Object> getThreadInfo() {
    Map<String, Object> valueHolder = inner.get();
    if (valueHolder == null) {
      valueHolder = new HashMap<>();
      inner.set(valueHolder);
    }
    return valueHolder;
  }

  @NotNull
  public static RequestContext.ReqCtx getCtx() {
    RequestContext.ReqCtx reqCtx = (RequestContext.ReqCtx) getThreadInfo().get(
        ThreadLocalRequestInfo.CTX.name());
    if (reqCtx == null) {
      reqCtx = new RequestContext.SimpleReqCtx();
      getThreadInfo().put(ThreadLocalRequestInfo.CTX.name(), reqCtx);
    }
    return (RequestContext.ReqCtx) getThreadInfo().get(
        ThreadLocalRequestInfo.CTX.name());
  }

  public static void clear() {
    inner.set(new HashMap<>());
  }

  public enum ThreadLocalRequestInfo {
    CTX,
  }

}

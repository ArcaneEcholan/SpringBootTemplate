package com.chaowen.springboottemplate.mvchooks;


import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class ThreadLocalUtil {

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
  public static ThreadLocalUtil.ReqCtx getCtx() {
    ReqCtx reqCtx =
        (ReqCtx) getThreadInfo().get(ThreadLocalRequestInfo.CTX.name());
    if (reqCtx == null) {
      reqCtx = new SimpleReqCtx();
      getThreadInfo().put(ThreadLocalRequestInfo.CTX.name(), reqCtx);
    }
    return (ReqCtx) getThreadInfo().get(ThreadLocalRequestInfo.CTX.name());
  }

  public static void set(String key, Object value) {
    getThreadInfo().put(key, value);
  }

  public static Object get(String key) {
    return getThreadInfo().get(key);
  }

  public static void clear() {
    inner.set(new HashMap<>());
  }

  public enum ThreadLocalRequestInfo {
    CTX,
  }

  public interface ReqCtx {

    Map<String, String> getHeaders();

    void setHeaders(Map<String, String> headers);

    void setStartTs(long startProcessTs);

    void setEndTs(long ts);

    long getCostMills();

  }

  public static class SimpleReqCtx implements ReqCtx {

    long startProcessTs;
    long endProcessTs;
    Map<String, String> headers;

    @Override
    public Map<String, String> getHeaders() {
      return this.headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
      this.headers = headers;
    }

    @Override
    public void setStartTs(long ts) {
      this.startProcessTs = ts;
    }

    @Override
    public void setEndTs(long ts) {
      this.endProcessTs = ts;
    }

    @Override
    public long getCostMills() {
      if (endProcessTs >= startProcessTs) {
        return endProcessTs - startProcessTs;
      }
      return 0;
    }

  }
}

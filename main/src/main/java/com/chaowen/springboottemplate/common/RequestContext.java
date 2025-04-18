package com.chaowen.springboottemplate.common;

import com.chaowen.springboottemplate.base.common.AuthLogin;
import com.chaowen.springboottemplate.utils.Token;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class RequestContext {

  public interface ReqCtx {

    void setHeaders(    Map<String, String> headers);

    Map<String, String>  getHeaders();

    AuthLogin getLoginAnno();

    void setLoginAnno(AuthLogin anno);

    @Nullable Token getToken();

    void setToken(Token token);

    void setStartTs(long startProcessTs);

    void setEndTs(long ts);

    long getCostMills();

    String getRequestId();

    void setRequestId(String requestId);
  }

  public static class SimpleReqCtx implements ReqCtx {

    AuthLogin loginAnno;
    Token token;
    long startProcessTs;
    long endProcessTs;
    String requestId;
    Map<String, String>  headers;
    @Override
    public void setHeaders(    Map<String, String>  headers) {
      this.headers = headers;
    }
    @Override
    public     Map<String, String>  getHeaders() {
      return this.headers ;
    }
    @Override
    public AuthLogin getLoginAnno() {
      return loginAnno;
    }

    @Override
    public void setLoginAnno(AuthLogin anno) {
      loginAnno = anno;
    }

    @Override
    public @Nullable Token getToken() {
      return token;
    }

    @Override
    public void setToken(Token token) {
      this.token = token;
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

    @Override
    public String getRequestId() {
      return requestId;
    }

    @Override
    public void setRequestId(String requestId) {
      this.requestId = requestId;
    }
  }
}

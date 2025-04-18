package com.chaowen.springboottemplate.base.module;

import static com.chaowen.springboottemplate.base.common.SimpleFactories.ofMap;

import com.chaowen.springboottemplate.base.common.*;
import com.chaowen.springboottemplate.base.common.JsonUtil;
import com.chaowen.springboottemplate.base.common.Ref;
import com.chaowen.springboottemplate.base.common.Utils;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import lombok.var;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.stereotype.Component;

@Component
class BasicPlatformStubImpl implements BasicPlatformStub {

  public static void main(String[] args) {
    var stub = new BasicPlatformStubImpl();

    //{
    //  var call = stub.call("/noway", null);
    //  System.out.println(call);
    //}
    //{
    //  var call = stub.call("/system/network/gateway", null);
    //  System.out.println(call);
    //}
  }

  @Override
  public String call(String path, Object body, String sockPath) {
    final var uri = PREFIX + path;
    var r = Utils.trycatch(() -> {
      var socketAddress = AFUNIXSocketAddress.of(Paths.get(sockPath));
      try (var socket = AFUNIXSocket.connectTo(socketAddress)) {
        // serialize the body to JSON (simple example)
        String jsonBody = body != null ? JsonUtil.toJsonString(body) : "{}";

        var strings = new ArrayList<String>();
        strings.add("application/json");
        var req = new PlainReq(uri, jsonBody, SimpleFactories.ofMap("Content-Type", strings));

        byte[] bytes = RawHttpConverter.req2Raw(req);

        // send the request
        try (var outputStream = socket.getOutputStream()) {
          outputStream.write(bytes);
          outputStream.flush();
        }

        // read the response
        var inputStream = socket.getInputStream();
        byte[] buffer = new byte[10240];
        int read = inputStream.read(buffer);
        if (read == -1) {
          return null;
        }

        var ref = Ref.of(PlainResp.class);
        var r1 = Utils.trycatch(() -> {
          var resp = RawHttpConverter.raw2Resp(
              new String(buffer, 0, read, StandardCharsets.UTF_8).getBytes(
                  StandardCharsets.UTF_8));
          ref.set(resp);
        });
        if (r1.hasEx()) {
          throw new RuntimeException(
              String.format("invalid unixsock response: %s", path),
              r1.getException());
        }

        var resp = ref.get();
        if (resp.getStatus() != 200) {
          if (resp.getStatus() == 404) {
            throw new RuntimeException(
                String.format("route not found: %s", path));
          } else {
            throw new RuntimeException(
                String.format("unknown status code: %s, %s", resp.getStatus(),
                    path));
          }
        } else {
          return resp.getBody();
        }
      }
    });
    if (r.hasEx()) {
      var ex = r.getRootCause();
      if (ex instanceof SocketException) {
        throw new RuntimeException("basic platform unixsock down",
            r.getException());
      }
      throw new RuntimeException("basic platform unixsock calling failed",
          r.getException());
    }
    return r.getValue();
  }

}

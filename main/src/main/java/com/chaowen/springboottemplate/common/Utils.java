package com.chaowen.springboottemplate.common;

import static com.chaowen.springboottemplate.base.common.Exceptions.genParamEx;

import java.net.InetAddress;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

@Slf4j
public class Utils {

  public static void throwModeExceptionInRange(String... modes) {
    throw genParamEx("mode not in " + Arrays.asList(modes));
  }

  @Deprecated
  public static void getCurrentThreadInfo() {
    log.debug("current thread: {} - {} - {} - {}",
        Thread.currentThread().getId(), Thread.currentThread().getName(),
        com.chaowen.springboottemplate.base.common.Utils.getParentOfCaller(),
        com.chaowen.springboottemplate.base.common.Utils.getSuperParentOfCaller());
  }

  public static String getIpAddr(HttpServletRequest request) {
    String ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.length() == 0 ||
        "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 ||
        "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 ||
        "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
      if ("127.0.0.1".equals(ipAddress) ||
          "0:0:0:0:0:0:0:1".equals(ipAddress)) {
        //根据网卡取本机配置的IP
        var r = com.chaowen.springboottemplate.base.common.Utils.trycatch(
            InetAddress::getLocalHost);
        if (r.hasEx()) {
          log.error("error read localhost: {}", r.getRootMessage());
        } else {
          ipAddress = r.getValue().getHostAddress();
        }
      }
    }
    //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
    if (ipAddress != null &&
        ipAddress.length() > 15) { //"***.***.***.***".length() = 15
      if (ipAddress.indexOf(",") > 0) {
        ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
      }
    }
    return ipAddress;
  }

}

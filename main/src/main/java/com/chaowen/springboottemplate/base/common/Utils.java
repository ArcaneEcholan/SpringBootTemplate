package com.chaowen.springboottemplate.base.common;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.IPAddressStringParameters;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

@Component
public class Utils {

  @NotNull
  public static String fmt(String format, Object... args) {
    return MessageFormatter.arrayFormat(format, args).getMessage();
  }

  @NotNull
  public static Throwable getRootCause(@NotNull Throwable t) {
    var rt = ExceptionUtil.getRootCause(t);
    if (rt == null) {
      return t;
    }
    return rt;
  }

  public static String getRootMsg(@NotNull Throwable t) {
    return getRootCause(t).getMessage();
  }

  @NotNull
  public static <T> TcResult<T> trycatch(@NotNull Provider<T> supplier) {
    try {
      return new TcResult<>(supplier.run(), null);
    } catch (Exception e) {
      return new TcResult<>(null, e);
    }
  }

  @NotNull
  public static TcResult<Object> trycatch(
      @NotNull Functions.Runnable supplier) {
    try {
      supplier.run();
      return new TcResult<>(null, null);
    } catch (Exception e) {
      return new TcResult<>(null, e);
    }
  }

  @Nullable
  public static Long longValue(@Nullable Object obj) {
    if (obj == null) {
      return null;
    }
    return ((Number) obj).longValue();
  }

  @Nullable
  public static Double doubleValue(@Nullable Object obj) {
    if (obj == null) {
      return null;
    }
    return ((Number) obj).doubleValue();
  }

  @Nullable
  public static Integer intValue(@Nullable String obj) {
    if (obj == null) {
      return null;
    }
    return Integer.valueOf(obj);
  }

  @Nullable
  public static Integer intValue(@Nullable Object obj) {
    if (obj == null) {
      return null;
    }
    return ((Number) obj).intValue();
  }

  @Nullable
  public static String toUpperCase(@Nullable String obj) {
    if (obj == null) {
      return null;
    }
    return obj.toUpperCase();
  }

  @Nullable
  public static String toLowerCase(@Nullable String obj) {
    if (obj == null) {
      return null;
    }
    return obj.toLowerCase();
  }

  @NotNull
  public static <T> T withDefault(@Nullable T obj, T theDefault) {
    if (obj == null) {
      return theDefault;
    }
    return obj;
  }

  @SneakyThrows
  @Nullable
  public static <T, R> R ifNotNull(@Nullable T obj, Functions.Function<T, R> mapping) {
    if (obj != null) {
      return mapping.apply(obj);
    }
    return null;
  }

  @Nullable
  public static Integer parseInt(String n) {
    var r = Utils.trycatch(() -> {
      return Integer.parseInt(n);
    });

    if (r.hasEx()) {
      return null;
    }

    return r.getValue();
  }

  public static void setIfNotNull(
      @NotNull Map map, @NotNull String key, @Nullable Object value) {
    if (value != null) {
      map.put(key, value);
    }
  }

  public interface Provider<T> {

    T run() throws Exception;
  }

  @Slf4j
  public static class IpUtils {

    // chinese not support
    private static final String DOMAIN_REGEX =
        "^(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.[A-Za-z]{2,6})+$";

    public static boolean isValidDomain(String domain) {
      return domain != null && domain.matches(DOMAIN_REGEX);
    }

    private static IPAddressStringParameters allowInetAtonIpParam() {
      return new IPAddressStringParameters.Builder().allow_inet_aton(false)
          .toParams();
    }

    public static boolean isIpv4Cidr(String cidr) {
      if (!isIpv4(cidr)) {
        return false;
      }
      return cidr.contains("/");
    }


    public static boolean isIpv6Cidr(String cidr) {
      if (!isIpv6(cidr)) {
        return false;
      }
      return cidr.contains("/");
    }

    public static boolean isIp(String ip) {
      return isIpv4(ip) || isIpv6(ip);
    }

    public static boolean isCidr(String ip) {
      return isIpv4Cidr(ip) || isIpv6Cidr(ip);
    }

    @SneakyThrows
    public static boolean isIpv4(String ip) {
      try {
        var ipAddress =
            new IPAddressString(ip, allowInetAtonIpParam()).toAddress();
        return ipAddress.getBitCount() == 32 && checkDash(ip);
      } catch (Throwable e) {
        log.warn("ip validation failed: {}", e.getMessage());
        return false;
      }
    }

    @SneakyThrows
    public static boolean isIpv6(String ip) {
      try {
        var ipAddress =
            new IPAddressString(ip, allowInetAtonIpParam()).toAddress();
        return ipAddress.getBitCount() == 128 && checkDash(ip);
      } catch (Throwable e) {
        log.warn("ip validation failed: {}", e.getMessage());
        return false;
      }
    }

    public static String getSubnet(String whiteListReq) {

      return new IPAddressString(whiteListReq).getAddress().toPrefixBlock()
          .toString();
    }

    public static boolean compareSubnet(String ip1, String ip2) {

      IPAddress addr1 = new IPAddressString(ip1).getAddress().toPrefixBlock();
      IPAddress addr2 = new IPAddressString(ip2).getAddress().toPrefixBlock();

      return addr1.toString().equals(addr2.toString());
    }

    private static boolean checkDash(@NotNull String ip) {
      return Pattern.compile("^[0-9/:.a-fA-F]+$").matcher(ip).matches();
    }
  }

  @Getter
  public static class TcResult<T> {

    private final T value;
    private final Exception exception;

    public TcResult(T value, Exception exception) {
      this.value = value;
      this.exception = exception;
    }

    @SneakyThrows
    public TcResult<T> ifFailed(java.lang.Runnable runnable) {
      if (exception != null) {
        runnable.run();
      }
      return this;
    }

    @SneakyThrows
    public TcResult<T> ifFailed(Functions.Consumer<TcResult<T>> runnable) {
      if (exception != null) {
        runnable.accept(this);
      }
      return this;
    }

    @SneakyThrows
    public TcResult<T> throwIfEx() {
      if (exception != null) {
        throw exception;
      }
      return this;
    }

    @SneakyThrows
    public TcResult<T> throwIfEx(
        @NotNull Functions.Function<TcResult<T>, Throwable> p) {
      if (exception != null) {
        throw p.apply(this);
      }
      return this;
    }

    @Nullable
    public String getMessage() {
      if (exception == null) {
        return null;
      }
      return exception.getMessage();
    }

    @Nullable
    public Throwable getCause() {
      if (exception == null) {
        return null;
      }
      if (exception.getCause() == null) {
        return exception;
      }
      return exception.getCause();
    }

    @Nullable
    public Throwable getRootCause() {
      if (exception == null) {
        return null;
      }
      return Utils.getRootCause(this.exception);
    }

    @Nullable
    public String getRootMessage() {
      if (!hasEx()) {
        return null;
      }
      return getRootMsg(this.exception);
    }

    public boolean hasEx() {
      return exception != null;
    }

    public boolean hasValue() {
      return value != null;
    }
  }

  @Deprecated
  public static String getParentOfCaller() {
    return "";
  }

  @Deprecated
  public static String getSuperParentOfCaller() {
    return "";
  }

  @Nullable
  public static <T> T getOne(BaseMapper<T> mapper, LambdaQueryWrapper<T> q) {
    var list = mapper.selectList(q);
    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

  public static <T> LambdaQueryWrapper<T> getQw(Class<T> clz) {
    return Wrappers.lambdaQuery();
  }

  @Nullable
  public static <T extends Enum<T>> T enumValueOf(Class<T> clz, String obj) {

    var r = trycatch(() -> Enum.valueOf(clz, obj));
    if (r.hasEx()) {
      return null;
    }

    return r.getValue();
  }
}

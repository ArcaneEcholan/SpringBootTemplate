package com.chaowen.springboottemplate.base.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.var;
import org.apache.commons.lang.exception.ExceptionUtils;
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
    var rt = ExceptionUtils.getRootCause(t);
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
  public static <T, R> R ifNotNull(
      @Nullable T obj, Functions.Function<T, R> mapping) {
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

  public interface Provider<T> {

    T run() throws Exception;
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
}

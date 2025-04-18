package com.chaowen.springboottemplate.base.common;

import org.jetbrains.annotations.Nullable;

public class Functions {

  @FunctionalInterface
  public interface BiConsumer<T, U> {

    void accept(T var1, U var2) throws Exception;

  }

  @FunctionalInterface
  public interface Function<T, R> {

    R apply(T var1) throws Exception;

  }

  @FunctionalInterface
  public interface Consumer<T> {

    void accept(T var1) throws Exception;

  }

  public interface Runnable {

    void run() throws Exception;
  }

  @FunctionalInterface
  public interface Provider<R> {

    @Nullable R apply() throws Exception;
  }
}

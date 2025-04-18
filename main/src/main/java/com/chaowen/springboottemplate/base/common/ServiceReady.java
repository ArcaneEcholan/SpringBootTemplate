package com.chaowen.springboottemplate.base.common;

import org.jetbrains.annotations.NotNull;

public interface ServiceReady {

  boolean required();

  boolean ready();

  @NotNull String serviceName();
}

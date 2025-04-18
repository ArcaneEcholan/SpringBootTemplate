package com.chaowen.springboottemplate.base.module;

import org.jetbrains.annotations.NotNull;

public interface AppConfig {

  String get(@NotNull String key);
}

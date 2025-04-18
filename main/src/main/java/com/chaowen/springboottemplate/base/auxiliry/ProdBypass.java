package com.chaowen.springboottemplate.base.auxiliry;

import static com.chaowen.springboottemplate.base.common.Utils.trycatch;

import com.chaowen.springboottemplate.base.common.*;
import com.chaowen.springboottemplate.base.common.EnvironmentChecker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Staticed
@Slf4j
public class ProdBypass {

  static EnvironmentChecker environmentChecker;

  static Environment env;

  @SneakyThrows
  public static void prodBypass(Runnable runnable, Logger log, String desc) {
    Boolean[] isBypassEnabledRef = new Boolean[]{false};
    boolean isProd = environmentChecker.isProdEnvironment();
    Utils.trycatch(() -> {
      boolean isBypassEnabled = Boolean.TRUE.equals(
          env.getProperty("dev.prod-bypass.enable", Boolean.class));
      isBypassEnabledRef[0] = isBypassEnabled;
    });
    if (!isProd || !isBypassEnabledRef[0]) {
      runnable.run();
    } else {
      log.debug("routine bypassed: {}", desc);
    }
  }
}

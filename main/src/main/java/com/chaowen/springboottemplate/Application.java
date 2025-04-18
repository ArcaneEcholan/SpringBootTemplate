package com.chaowen.springboottemplate;

import com.chaowen.springboottemplate.base.BeforeBeanInitializer;
import com.chaowen.springboottemplate.base.common.Utils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableCaching(proxyTargetClass = true)
@EnableTransactionManagement
@EnableScheduling
public class Application {


  public static final String APP_SCAN_PACKAGE =
      "com.chaowen.springboottemplate";

  public static void main(String[] args) {
    log.debug("========== app starting... ==========");

    var r = Utils.trycatch(() -> {
      var app = new SpringApplication(Application.class);

      // InetAddress.getLocalHost().getHostName() took too long
      app.setLogStartupInfo(false);

      app.addListeners(new BeforeBeanInitializer());
      app.run(args);
    });

    if (r.hasEx()) {
      log.error("app start failed, {}", r.getRootMessage());
    } else {
      log.info("========== app started ==========");
    }
  }

}

package com.chaowen.springboottemplate;

import com.chaowen.springboottemplate.base.BeforeBeanInitializer;
import com.chaowen.springboottemplate.base.common.Utils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"com.chaowen.springboottemplate"})
@MapperScan(basePackages = {"com.chaowen.springboottemplate"},
    annotationClass = Mapper.class)
class PackageScan {

}

@Configuration
class CommonAppFunctions {

}

@Slf4j
@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
@EnableTransactionManagement
@EnableScheduling
@Import({PackageScan.class})
public class App {

  public static void main(String[] args) {
    log.debug("========== app starting... ==========");

    var r = Utils.trycatch(() -> {
      var app = new SpringApplication(App.class);

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

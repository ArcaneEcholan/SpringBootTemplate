package com.chaowen.springboottemplate;

import com.chaowen.springboottemplate.base.common.Utils;
import com.chaowen.springboottemplate.base.BeforeBeanInitializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication(exclude = {LdapAutoConfiguration.class})
@EnableCaching(proxyTargetClass = true)
@EnableTransactionManagement
@EnableScheduling
@interface CommonAppConf {

}

@Configuration
@ComponentScan(basePackages = {"com.chaowen.springboottemplate"})
class PackageRef {

}

@Slf4j
@CommonAppConf
@Import(PackageRef.class)
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

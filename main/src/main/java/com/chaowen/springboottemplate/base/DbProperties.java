package com.chaowen.springboottemplate.base;

import javax.annotation.PostConstruct;
import com.chaowen.springboottemplate.base.common.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "db")
public class DbProperties {

  @Getter
  private String user = "root";
  @Getter
  private String password = "root";
  private String name = "totp-admin";
  private String host = "localhost";
  private String port = "3306";

  private String url;

  public static String formUrl(String host, String port, String name) {
    return Utils.fmt(
        "jdbc:mysql://{}:{}/{}?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
        host, port, name);
  }

  public static String formUrlWithoutDbName(String host, String port) {
    return Utils.fmt(
        "jdbc:mysql://{}:{}?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
        host, port);
  }

  public String getUrl() {
    return formUrl(host, port, name);
  }
}

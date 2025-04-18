package com.chaowen.springboottemplate.base;

import javax.annotation.PostConstruct;
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

  @Getter
  private String url;

  @PostConstruct
  public void init() {
    url = formUrl(host, Integer.valueOf(port), name);
    log.debug("data source config: host={} port={} username={} password={}",
        host, port, user, password);
  }

  public static String formUrl(String host, int port, String name) {
    return String.format(
        "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
        host, port, name);
  }
}

package com.chaowen.springboottemplate.base.common;

import static com.chaowen.springboottemplate.base.common.Consts.AppProfiles.DEV;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Data
@Component
public class EnvironmentChecker {
  //todo 待刪除 (cz: 用于网络回滚测试用例)

  @Autowired
  private Environment environment;
  private String msg = "";

  public boolean isTestEnvironment() {
    String[] activeProfiles = environment.getActiveProfiles();
    for (String profile : activeProfiles) {
      if ("test".equalsIgnoreCase(profile)) {
        return true;
      }
    }
    return false;
  }

  public boolean isProdEnvironment() {
    String[] activeProfiles = environment.getActiveProfiles();
    for (String profile : activeProfiles) {
      if ("prod".equalsIgnoreCase(profile)) {
        return true;
      }
    }
    return false;
  }

  public boolean isDefaultEnvironment() {
    String[] activeProfiles = environment.getActiveProfiles();
    for (String profile : activeProfiles) {
      if (DEV.equalsIgnoreCase(profile)) {
        return true;
      }
    }
    return false;
  }
}

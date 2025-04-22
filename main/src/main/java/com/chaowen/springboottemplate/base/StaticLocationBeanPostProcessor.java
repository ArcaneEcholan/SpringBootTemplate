package com.chaowen.springboottemplate.base;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StaticLocationBeanPostProcessor implements BeanPostProcessor {

  @Autowired
  private Environment environment;

  public static boolean isValidDirectory(String path) {
    if (path.isEmpty()) {
      return false;
    }

    File file = new File(path);

    if (!file.isAbsolute()) {
      log.warn("'serve_static' must be absolute path");
      return false;
    }
    if (!path.endsWith(File.separator)) {
      log.warn("'serve_static' must end with " + File.separator);
      return false;
    }
    if (!file.exists()) {
      log.warn("'serve_static' not exist");
      return false;
    }
    if (!file.isDirectory()) {
      log.warn("'serve_static' must be a dir");
      return false;
    }

    return true;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean.getClass() == WebProperties.class) {
      String serveStatic = environment.getProperty("serve_static");
      if (serveStatic == null) {
        return bean;
      }

      if (isValidDirectory(serveStatic)) {
        WebProperties w = (WebProperties) bean;
        String[] staticLocations = w.getResources().getStaticLocations();
        List<String> collect =
            Arrays.stream(staticLocations).collect(Collectors.toList());
        collect.add("file:" + serveStatic);
        w.getResources().setStaticLocations(collect.toArray(new String[]{}));
        return bean;
      }

      throw new RuntimeException("'serve_static' invalid");
    }

    return bean;
  }
}

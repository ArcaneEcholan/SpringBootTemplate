package com.chaowen.springboottemplate.base.auxiliry;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Objects;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
class DefaultBeanPostProcessor implements BeanPostProcessor {

  private final Logger log = Objects.requireNonNull(
      LoggerFactory.getLogger(DefaultBeanPostProcessor.class));

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    for (Field field : bean.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(ProfileBean.class)) {
        //log.debug("resolve profilebean: {}.{}", beanName, field.getName());
        Class<?> interfaceType = field.getType();
        var p = applicationContext.getBeanNamesForType(interfaceType);
        Object b = null;
        if (p.length > 0) {
          b = applicationContext.getBean(p[0]);
          //log.debug("wire bean: {}", b);
        } else {
          b = Proxy.newProxyInstance(interfaceType.getClassLoader(),
              new Class<?>[]{interfaceType}, (proxy, method, args) -> {
                log.trace(
                    "no implementation found for " + interfaceType.getName());
                return null;
              });
          //log.debug("wire a proxy");
        }

        Object targetBean = b;
        field.setAccessible(true);
        try {
          field.set(bean, targetBean);
        } catch (IllegalAccessException e) {
          throw new RuntimeException("failed to set default proxy bean", e);
        }
      }
    }
    return bean;
  }
}

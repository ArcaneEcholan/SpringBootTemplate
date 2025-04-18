package com.chaowen.springboottemplate.base;

import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AfterBeanInitializer implements SmartInitializingSingleton {

  public interface AfterBeanInitHook {

    void run();
  }

  public interface AfterBeanInitOrder {

    @SneakyThrows
    void getOrderedHooks(List<AfterBeanInitHook> hooks);
  }

  @Autowired
  List<AfterBeanInitOrder> abstractOrders;
  @SneakyThrows
  @Override
  public void afterSingletonsInstantiated() {
    log.info("==service init area==");

    var hooks = new ArrayList<AfterBeanInitHook>();
    abstractOrders.forEach(it -> {
      it.getOrderedHooks(hooks);
    });

    // ordered services init area
    hooks.forEach(AfterBeanInitHook::run);
  }


}

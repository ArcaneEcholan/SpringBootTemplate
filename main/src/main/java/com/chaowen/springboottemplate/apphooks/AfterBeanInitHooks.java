package com.chaowen.springboottemplate.apphooks;

import com.chaowen.springboottemplate.base.AfterBeanInitializer.AfterBeanInitHook;
import com.chaowen.springboottemplate.base.AfterBeanInitializer.AfterBeanInitOrder;
import com.chaowen.springboottemplate.base.DatabaseConfigurer.DatabaseTableInitHook;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class AfterBeanInitHooks {

  /**
   * Control the order of hooks here
   */
  @Component
  public static class Order implements AfterBeanInitOrder {

    @Autowired(required = false)
    private DatabaseTableInitHook databaseTableInit;
    // declare more hooks here ...

    @Override
    public void getOrderedHooks(List<AfterBeanInitHook> hooks) {
      if (databaseTableInit != null) {
        hooks.add(databaseTableInit);
      }
      // add more hooks here ...
    }
  }


}

package com.chaowen.springboottemplate.base;

import com.chaowen.springboottemplate.apphooks.BeforeBeanInitHooks.TableSuffixInitHook;
import com.chaowen.springboottemplate.apphooks.TableNameMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;


@Slf4j
public class BeforeBeanInitializer
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  @SneakyThrows
  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    log.info("==Before All==");

    SpringEnvWrapper.environment = event.getEnvironment();
    var customPropsMap = new HashMap<String, Object>();

    final var environment = event.getEnvironment();

    var hooks = new ArrayList<BeforeBeanInitHook>();
    var abstractOrders =
        InterfaceFinder.findAllImpls(BeforeBeanInitOrder.class);
    abstractOrders.forEach(it -> {
      BeforeBeanInitOrder beforeBeanInitOrder = null;
      try {
        beforeBeanInitOrder = it.newInstance();
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      beforeBeanInitOrder.getOrderedHooks(hooks);
    });

    // ordered services init area
    hooks.forEach((it) -> {
      it.run(environment, customPropsMap);
    });

    environment.getPropertySources()
        .addFirst(new MapPropertySource("custom-props-source", customPropsMap));
  }

  public interface BeforeBeanInitHook {

    void run(Environment env, Map<String, Object> customPropsMap);
  }

  public interface BeforeBeanInitOrder {

    void getOrderedHooks(List<BeforeBeanInitHook> hooks);
  }

  public static class SpringEnvWrapper {

    static Environment environment;

    public static TableNameMapper getTableNameMapper() {
      return environment.getProperty(TableSuffixInitHook.TABLE_NAME_MAPPER, TableNameMapper.class);
    }

  }

}

package com.chaowen.springboottemplate.base.business;

import com.chaowen.springboottemplate.base.common.Functions;
import java.lang.reflect.Field;
import lombok.SneakyThrows;
import lombok.var;

public class MonitorHelpers {

  public static final Functions.BiConsumer<Object, Field> outBound = (obj, field) -> {
    var fieldValue = (Long) field.get(obj);
    if (fieldValue != null && fieldValue == -1) {
      field.set(obj, null);
    }
  };
  public static final Functions.BiConsumer<Object, Field> inBound = (obj, field) -> {
    var fieldValue = (Long) field.get(obj);
    if (fieldValue == null) {
      field.set(obj, -1L);
    }
  };

  @SneakyThrows
  public static void convertMonitorParams(
      Object[] configObjects, Functions.BiConsumer<Object, Field> fieldEditor) {
    for (Object config : configObjects) {
      if (config != null) {
        for (Field field : config.getClass().getDeclaredFields()) {
          field.setAccessible(true);

          if (field.getType().equals(Long.class)) {
            fieldEditor.accept(config, field);
          }
        }
      }
    }
  }
}

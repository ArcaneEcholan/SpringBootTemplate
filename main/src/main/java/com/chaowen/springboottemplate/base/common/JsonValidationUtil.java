package com.chaowen.springboottemplate.base.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chaowen.springboottemplate.base.auxiliry.Staticed;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

@Component
public class JsonValidationUtil {

  @NotNull
  private static final Logger log =
      Objects.requireNonNull(LoggerFactory.getLogger(JsonValidationUtil.class));
  @Staticed
  static ObjectMapper objectMapper;
  @Staticed
  static Validator validator;


  private static boolean isValidRule(Object ruleKey, Map map) {
    if (ruleKey instanceof String) {
      return map.containsKey(ruleKey);
    } else if (ruleKey instanceof Predicate) {
      return ((Predicate<Map>) ruleKey).test(map);
    }
    return false;
  }

  public static Map validateJson(
      String json, Map<Object, BiConsumer<String, Map>> validationRules) {
    var map = JsonUtil.parse(json);
    for (var entry : validationRules.entrySet()) {
      if (isValidRule(entry.getKey(), map)) {
        entry.getValue().accept(json, map);
        return map;
      }
    }
    throw Exceptions.genParamEx("json structure not supported");
  }

  @SneakyThrows
  public static <T> T parseAndValidateJson(
      @NotNull String json, Class<T> clazz) {

    T obj = null;
    try {
      obj = objectMapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new HttpMessageNotReadableException(e.getMessage(), e);
    }

    var violations = validator.validate(obj);
    if (violations != null && !violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    return obj;
  }
}

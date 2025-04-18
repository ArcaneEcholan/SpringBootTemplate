package com.chaowen.springboottemplate.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class JsonReqBodyCoercions {

  @SneakyThrows
  static String getParamName(JsonParser p) {
    var name = p.getCurrentName();
    return name == null ? "" : name.equals("null") ? "" : name;
  }

  @Configuration
  public static class JacksonInputTypeCoercionRegister {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
      return builder -> {
        builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        builder.deserializers(new BooleanDeserializer());
        builder.deserializers(new FloatDeserializer());
        builder.deserializers(new DoubleDeserializer());
        builder.deserializers(new LongDeserializer());
        builder.deserializers(new IntegerDeserializer());
        builder.deserializers(new StringDeserializer());
      };
    }
  }

  public static class JsonDeserializeException extends RuntimeException {

    public JsonDeserializeException(String msg) {
      super(msg);
    }
  }

  @Slf4j
  static class FloatDeserializer extends JsonDeserializer<Float> {

    public Class<?> handledType() {
      return Float.class;
    }

    @Override
    public Float deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      log.debug("[{}] receive : [{}] with value: [{}] ",
          this.getClass().getSimpleName(), getParamName(p),
          p.getValueAsString());
      JsonToken token = p.getCurrentToken();
      if (token.isNumeric()) {
        var big = new BigDecimal(p.getValueAsString());
        if (String.valueOf(big.floatValue()).equals("Infinity")) {
          var errMsg = String.format(
              "%s(line: %d, col: %d): break the maximum limit of float",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
          throw new JsonDeserializeException(errMsg);
        } else if (String.valueOf(big.floatValue()).equals("-Infinity")) {
          var errMsg = String.format(
              "%s(line: %d, col: %d): break the minimum limit of float",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
          throw new JsonDeserializeException(errMsg);
        }
        return p.getFloatValue();
      }

      var errMsg = String.format("%s(line: %d, col: %d): only float is allowed",
          getParamName(p), p.getTokenLocation().getLineNr(),
          p.getTokenLocation().getColumnNr());
      throw new JsonDeserializeException(errMsg);
    }
  }

  @Slf4j
  static class LongDeserializer extends JsonDeserializer<Long> {

    public Class<?> handledType() {
      return Long.class;
    }

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      log.debug("[{}] receive : [{}] with value: [{}] ",
          this.getClass().getSimpleName(), getParamName(p),
          p.getValueAsString());
      var token = p.getCurrentToken();
      if (token.isNumeric()) {
        var big = new BigDecimal(p.getValueAsString());
        if (String.valueOf(big).contains(".")) {
          var errMsg = String.format(
              "%s(line: %d, col: %d): only long(-9,223,372,036,854,775,808 to 9,223,372,036,854,775,807) is allowed",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
          throw new JsonDeserializeException(errMsg);
        }
        try {
          big = big.setScale(0, RoundingMode.HALF_UP);
          return big.longValueExact();
        } catch (ArithmeticException e) {
          if (e.getMessage().equalsIgnoreCase("overflow")) {
            var errMsg = String.format(
                "%s(line: %d, col: %d): break the maximum/minimum limit of long(-9,223,372,036,854" +
                ",775,808 to 9,223,372,036,854,775,807)", getParamName(p),
                p.getTokenLocation().getLineNr(),
                p.getTokenLocation().getColumnNr());
            throw new JsonDeserializeException(errMsg);
          }
        }
      }

      var errMsg = String.format(
          "%s(line: %d, col: %d): only long(-9,223,372,036,854,775,808 to 9,223,372,036,854,775,807) is allowed",
          getParamName(p), p.getTokenLocation().getLineNr(),
          p.getTokenLocation().getColumnNr());
      throw new JsonDeserializeException(errMsg);
    }
  }

  @Slf4j
  static class StringDeserializer extends JsonDeserializer<String> {

    public Class<?> handledType() {
      return String.class;
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      log.debug("[{}] receive : [{}] with value: [{}] ",
          this.getClass().getSimpleName(), getParamName(p),
          p.getValueAsString());
      var token = p.getCurrentToken();
      if (token.isNumeric() || token.isBoolean()) {
        var errMsg =
            String.format("%s(line: %d, col: %d): only string is allowed",
                getParamName(p), p.getTokenLocation().getLineNr(),
                p.getTokenLocation().getColumnNr());
        throw new JsonDeserializeException(errMsg);
      }
      return p.getValueAsString();
    }
  }

  @Slf4j
  static class IntegerDeserializer extends JsonDeserializer<Integer> {

    public Class<?> handledType() {
      return Integer.class;
    }

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      log.debug("[{}] receive : [{}] with value: [{}] ",
          this.getClass().getSimpleName(), getParamName(p),
          p.getValueAsString());
      var token = p.getCurrentToken();
      if (token.isNumeric()) {
        var big = new BigDecimal(p.getValueAsString());
        if (String.valueOf(big).contains(".")) {
          var errMsg = String.format(
              "%s(line: %d, col: %d): only integer(-2,147,483,648 to 2,147,483,647) is allowed",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
          throw new JsonDeserializeException(errMsg);
        }
        try {
          big = big.setScale(0, RoundingMode.HALF_UP);
          return big.intValueExact();
        } catch (ArithmeticException e) {
          if (e.getMessage().equalsIgnoreCase("overflow")) {
            var errMsg = String.format(
                "%s(line: %d, col: %d): break the maximum/minimum limit of integer(-2,147,483,648 to 2,147,483,647)",
                getParamName(p), p.getTokenLocation().getLineNr(),
                p.getTokenLocation().getColumnNr());
            throw new JsonDeserializeException(errMsg);
          }
        }
      }

      var errMsg = String.format(
          "%s(line: %d, col: %d): only integer(-2,147,483,648 to 2,147,483,647) is allowed",
          getParamName(p), p.getTokenLocation().getLineNr(),
          p.getTokenLocation().getColumnNr());
      throw new JsonDeserializeException(errMsg);
    }
  }

  @Slf4j
  static class DoubleDeserializer extends JsonDeserializer<Double> {

    public Class<?> handledType() {
      return Double.class;
    }

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      log.debug("[{}] receive : [{}] with value: [{}] ",
          this.getClass().getSimpleName(), getParamName(p),
          p.getValueAsString());
      JsonToken token = p.getCurrentToken();
      if (token.isNumeric()) {
        var big = new BigDecimal(p.getValueAsString());
        if (String.valueOf(big.doubleValue()).equals("Infinity")) {
          var errMsg = String.format(
              "%s(line: %d, col: %d):  break the maximum/minimum limit of double",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
          throw new JsonDeserializeException(errMsg);
        } else if (String.valueOf(big.doubleValue()).equals("-Infinity")) {
          var errMsg = String.format(
              "%s(line: %d, col: %d):  break the minimum limit of double",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
          throw new JsonDeserializeException(errMsg);
        }
        return p.getDoubleValue();
      }

      var errMsg =
          String.format("%s(line: %d, col: %d): only double is allowed",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
      throw new JsonDeserializeException(errMsg);
    }
  }

  @Slf4j
  static class BooleanDeserializer extends JsonDeserializer<Boolean> {

    public Class<?> handledType() {
      return Boolean.class;
    }

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      log.debug("[{}] receive : [{}] with value: [{}] ",
          this.getClass().getSimpleName(), getParamName(p),
          p.getValueAsString());
      var token = p.getCurrentToken();
      if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
        return p.getBooleanValue();
      }
      var errMsg =
          String.format("%s(line: %d, col: %d): only boolean is allowed",
              getParamName(p), p.getTokenLocation().getLineNr(),
              p.getTokenLocation().getColumnNr());
      throw new JsonDeserializeException(errMsg);
    }
  }
}

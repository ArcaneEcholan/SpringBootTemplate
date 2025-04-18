package com.chaowen.springboottemplate.base.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface AuthLogin {

  public static enum Type {
    // auth cluster node identification
    CLUSTER,
    // auth normal user identification(this is default)
    USER
  }

  Type type() default Type.USER;

}



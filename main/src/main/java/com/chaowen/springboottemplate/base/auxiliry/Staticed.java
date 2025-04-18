package com.chaowen.springboottemplate.base.auxiliry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Staticed {

  @AliasFor("inject") boolean value() default true;

  boolean inject() default true;
}

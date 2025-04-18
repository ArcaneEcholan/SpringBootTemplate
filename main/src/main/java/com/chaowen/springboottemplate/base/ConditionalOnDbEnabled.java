package com.chaowen.springboottemplate.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(prefix = "db",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false)
public @interface ConditionalOnDbEnabled {

}

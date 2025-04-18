package com.chaowen.springboottemplate.base.common;

import org.springframework.web.servlet.HandlerInterceptor;

public interface OrderedHandlerInterceptor extends HandlerInterceptor {
    int getOrder();
}

package com.chaowen.springboottemplate.base.module;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProductAppCtx {

  boolean startSuccess = false;
  Throwable startFailEx = null;
  App app;
  String networkModel = "";
}

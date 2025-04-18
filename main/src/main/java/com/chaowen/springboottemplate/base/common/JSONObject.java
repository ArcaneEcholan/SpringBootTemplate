package com.chaowen.springboottemplate.base.common;

import java.util.LinkedHashMap;

//CHECKSTYLE:OFF
public class JSONObject<K, V> extends LinkedHashMap<K, V> {

  public JSONObject<K, V> fluentPut(K key, V value) {
    this.put(key, value);
    return this;
  }
}
//CHECKSTYLE:ON

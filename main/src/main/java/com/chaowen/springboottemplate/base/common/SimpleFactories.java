package com.chaowen.springboottemplate.base.common;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.var;

public class SimpleFactories {

  public static <T> T[] ofArr(T... arr) {
    return arr;
  }

  public static List ofArr(Map... arr) {
    var r = new ArrayList();
    r.addAll(Arrays.asList(arr));
    return r;
  }

  public static <T> List<T> ofList(T... elems) {
    return new ArrayList<T>(asList(elems));
  }

  // region ofMap
  public static <K, V> Map<K, V> ofMap0(Object... input) {

    if ((input.length & 1) != 0) {
      throw new IllegalArgumentException("length is odd");
    }

    HashMap<K, V> kvHashMap = new LinkedHashMap<>();
    for (int i = 0; i < input.length; i += 2) {
      K k = (K) input[i];
      V v = (V) input[i + 1];
      kvHashMap.put(k, v);
    }
    return kvHashMap;
  }

  public static <K, V> Map<K, V> ofMap() {
    return ofMap0();
  }

  public static <K, V> Map<K, V> ofMap(K k1, V v1) {
    return ofMap0(k1, v1);
  }

  public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2) {
    return ofMap0(k1, v1, k2, v2);
  }

  public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3) {
    return ofMap0(k1, v1, k2, v2, k3, v3);
  }

  public static <K, V> Map<K, V> ofMap(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return ofMap0(k1, v1, k2, v2, k3, v3, k4, v4);
  }

  public static <K, V> Map<K, V> ofMap(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return ofMap0(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
  }

  public static <K, V> Map<K, V> ofMap(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    return ofMap0(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
  }

  public static <K, V> Map<K, V> ofMap(
      K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6,
      K k7, V v7) {
    return ofMap0(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
  }
  // endregion

  // region ofJson

  public static Map ofJson() {
    return new LinkedHashMap();
  }

  public static Map ofJson(String key, Object value) {
    Map map = new LinkedHashMap();
    map.put(key, value);
    return map;
  }

  public static Map ofJson(String k1, Object v1, String k2, Object v2) {
    Map json = ofJson(k1, v1);
    json.put(k2, v2);
    return json;
  }


  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    Map json = ofJson(k1, v1, k2, v2);
    json.put(k3, v3);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4) {
    Map json = ofJson(k1, v1, k2, v2, k3, v3);
    json.put(k4, v4);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5) {
    Map json = ofJson(k1, v1, k2, v2, k3, v3, k4, v4);
    json.put(k5, v5);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5, String k6, Object v6) {
    Map json = ofJson(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    json.put(k6, v6);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5, String k6, Object v6,
      String k7, Object v7) {
    Map json = ofJson(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    json.put(k7, v7);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5, String k6, Object v6,
      String k7, Object v7, String k8, Object v8) {
    Map json = ofJson(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    json.put(k8, v8);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5, String k6, Object v6,
      String k7, Object v7, String k8, Object v8, String k9, Object v9) {
    Map json =
        ofJson(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    json.put(k9, v9);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5, String k6, Object v6,
      String k7, Object v7, String k8, Object v8, String k9, Object v9,
      String k10, Object v10) {
    Map json =
        ofJson(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8,
            k9, v9);
    json.put(k10, v10);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5, String k6, Object v6,
      String k7, Object v7, String k8, Object v8, String k9, Object v9,
      String k10, Object v10, String k11, Object v11) {
    Map json =
        ofJson(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8,
            k9, v9, k10, v10);
    json.put(k11, v11);
    return json;
  }

  public static Map ofJson(
      String k1, Object v1, String k2, Object v2, String k3, Object v3,
      String k4, Object v4, String k5, Object v5, String k6, Object v6,
      String k7, Object v7, String k8, Object v8, String k9, Object v9,
      String k10, Object v10, String k11, Object v11, String k12, Object v12) {
    Map json =
        ofJson(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8,
            k9, v9, k10, v10, k11, v11);
    json.put(k12, v12);
    return json;
  }

  // endregion

}

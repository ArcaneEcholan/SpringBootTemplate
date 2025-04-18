package com.chaowen.springboottemplate.base.common;

public class Ref<T> {

  T theRef;

  private Ref() {
  }

  public static <T0> Ref<T0> of(Class<T0> clazz) {
    Ref<T0> ref = new Ref<>();
    ref.theRef = null;
    return ref;
  }

  public void set(T b) {
    this.theRef = b;
  }

  public T get() {
    return this.theRef;
  }
}

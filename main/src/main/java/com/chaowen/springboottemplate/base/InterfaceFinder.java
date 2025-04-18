package com.chaowen.springboottemplate.base;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class InterfaceFinder {

  public static <T> List<Class<? extends T>> findAllImpls(Class<T> interfaceClass) {
    Reflections reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage(getRootPackage(interfaceClass)))
        .setScanners(Scanners.SubTypes));

    Set<Class<? extends T>> classes = reflections.getSubTypesOf(interfaceClass);

    return classes.stream()
        .filter(c -> !c.isInterface() && !java.lang.reflect.Modifier.isAbstract(c.getModifiers()))
        .collect(Collectors.toList());
  }

  private static <T> String getRootPackage(Class<T> clazz) {
    Package pkg = clazz.getPackage();
    return (pkg != null) ? pkg.getName() : "";
  }
}

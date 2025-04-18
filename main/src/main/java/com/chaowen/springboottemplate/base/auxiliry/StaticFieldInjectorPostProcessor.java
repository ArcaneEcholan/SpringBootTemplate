package com.chaowen.springboottemplate.base.auxiliry;

import java.lang.reflect.Field;
import lombok.var;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class StaticFieldInjectorPostProcessor
    implements BeanPostProcessor, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    Class<?> beanClass = bean.getClass();

    boolean isClassAnnotatedWithStaticed =
        beanClass.isAnnotationPresent(Staticed.class);

    for (Field field : beanClass.getDeclaredFields()) {
      if (isStatic(field)) {
        var fieldAnnotation = field.getAnnotation(Staticed.class);
        if (isClassAnnotatedWithStaticed &&
            (fieldAnnotation == null || fieldAnnotation.inject())) {
          injectStaticField(field);
        } else if (fieldAnnotation != null && fieldAnnotation.inject()) {
          injectStaticField(field);
        }
      }
    }
    return bean;
  }

  private boolean isStatic(Field field) {
    return java.lang.reflect.Modifier.isStatic(field.getModifiers());
  }

  private void injectStaticField(Field field) {
    try {

      Object beanToInject = null;

      Class<?> fieldType = field.getType();
      if (fieldType == ApplicationEventPublisher.class) {
        beanToInject = applicationContext;
      }

      Resource1 resource = field.getAnnotation(Resource1.class);
      if (resource != null && !resource.value().isEmpty()) {
        // If @Resource with a name is specified, inject by name and throw error if not found
        try {
          beanToInject = applicationContext.getBean(resource.value());
        } catch (NoSuchBeanDefinitionException e) {
          throw new BeansException(
              "No bean found for name: " + resource.value() +
              " required by static field " + field.getName()) {
          };
        }
      } else {
        // No @Resource or no name specified, attempt to inject by type
        Class<?> fieldType1 = field.getType();
        String[] beanNamesForType =
            applicationContext.getBeanNamesForType(fieldType1);
        if (beanNamesForType.length > 0) {
          // Inject the first bean found for the field type if available
          beanToInject = applicationContext.getBean(beanNamesForType[0]);
        }
        // If no beans found, leave as null
      }

      if (beanToInject != null) {
        field.setAccessible(true);
        field.set(null, beanToInject);
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(
          "Failed to inject static field: " + field.getName(), e);
    }
  }
}

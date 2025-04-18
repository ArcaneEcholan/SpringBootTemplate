package com.chaowen.springboottemplate.base;

import static com.chaowen.springboottemplate.base.BeforeBeanInitializer.SpringEnvWrapper.getTableNameMapper;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.chaowen.springboottemplate.base.common.OrderedHandlerInterceptor;
import java.util.Comparator;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Slf4j
@Configuration
public class BeansFactory {

  // global task scheduler
  @Bean
  public ThreadPoolTaskExecutor threadpool() {
    final int corePoolSize = Runtime.getRuntime().availableProcessors();
    final int maxPoolSize = Runtime.getRuntime().availableProcessors();

    var executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(Integer.MAX_VALUE);
    String namePrefix = "custom-thread-pool-";
    executor.setThreadNamePrefix(namePrefix);
    executor.setRejectedExecutionHandler(
        new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(false);
    executor.initialize();
    return executor;
  }

  // global mybatis tablename mapper
  @Bean
  public MybatisPlusInterceptor tablePrefixInterceptor() {
    var interceptor = new MybatisPlusInterceptor();
    var dynamicTableNameInnerInterceptor =
        new DynamicTableNameInnerInterceptor();
    dynamicTableNameInnerInterceptor.setTableNameHandler(
        (sql, tableName) -> getTableNameMapper().map(tableName));
    interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
    return interceptor;
  }

  // mysql datasource
  @Bean
  public DataSource dataSource(DbProperties dbProperties) {
    DruidDataSource dataSource = new DruidDataSource();
    dataSource.setConnectionErrorRetryAttempts(5);
    dataSource.setTimeBetweenConnectErrorMillis(5000);
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl(dbProperties.getUrl());
    dataSource.setUsername(dbProperties.getUser());
    dataSource.setPassword(dbProperties.getPassword());
    return dataSource;
  }

  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    var interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(
        new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }

  // mvc interceptors
  @Bean
  public WebMvcConfigurer interceptorConfigurer(ApplicationContext appCtx) {
    return new WebMvcConfigurer() {

      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        appCtx.getBeansOfType(OrderedHandlerInterceptor.class).values().stream()
            .sorted(
                Comparator.comparingInt(OrderedHandlerInterceptor::getOrder))
            .collect(Collectors.toList())
            .forEach(i -> registry.addInterceptor(i).addPathPatterns("/**"));
      }
    };

  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS", "PATCH")
            .allowCredentials(true).maxAge(3600);
      }
    };
  }
}


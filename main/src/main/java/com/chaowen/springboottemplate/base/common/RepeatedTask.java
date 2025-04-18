package com.chaowen.springboottemplate.base.common;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

public class RepeatedTask {

  public enum Status {
    RUNNING,
    STOPPED
  }

  public static class Config {

    public String name;
    public long executeIntervalMs;
    public String cronExpression;
    public long waitStartMs;
    public long waitStopMs;

    public Runnable task;
    public Runnable preprocess;
    public Runnable postprocess;
    public Supplier<Boolean> stopTrigger;
  }

  private final Config config;
  private final ThreadPoolTaskScheduler scheduler;
  private ScheduledFuture<?> future;
  private volatile Status status = Status.STOPPED;

  public RepeatedTask(Config config) {
    if (config.name == null || config.name.trim().isEmpty()) {
      config.name = generateAutoName();
    }
    config.name = "repeat-task-" + config.name;
    this.config = config;

    this.scheduler = new ThreadPoolTaskScheduler();
    this.scheduler.setPoolSize(1);
    this.scheduler.setThreadNamePrefix(config.name + "-");
    this.scheduler.initialize();
  }

  public synchronized void start() {
    if (status == Status.RUNNING) {
      return;
    }

    if (config.preprocess != null) {
      config.preprocess.run();
    }

    if (config.waitStartMs > 0) {
      try {
        Thread.sleep(config.waitStartMs);
      } catch (InterruptedException ignored) {
      }
    }

    status = Status.RUNNING;

    Runnable wrappedTask = () -> {
      if (config.stopTrigger != null && config.stopTrigger.get()) {
        stop();
        return;
      }
      if (config.task != null) {
        config.task.run();
      }
    };

    if (config.cronExpression != null && !config.cronExpression.isEmpty()) {
      future = scheduler.schedule(wrappedTask,
          new CronTrigger(config.cronExpression));
    } else if (config.executeIntervalMs > 0) {
      future =
          scheduler.scheduleAtFixedRate(wrappedTask, config.executeIntervalMs);
    } else {
      throw new IllegalArgumentException(
          "either cronExpression or executeIntervalMs must be set");
    }
  }

  public synchronized void stop() {
    if (status == Status.STOPPED) {
      return;
    }

    status = Status.STOPPED;

    if (future != null) {
      future.cancel(true);
      future = null;
    }

    if (config.waitStopMs > 0) {
      try {
        Thread.sleep(config.waitStopMs);
      } catch (InterruptedException ignored) {
      }
    }

    if (config.postprocess != null) {
      config.postprocess.run();
    }

    scheduler.shutdown();
  }

  public Status status() {
    return status;
  }

  private static String generateAutoName() {
    String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    return System.currentTimeMillis() + uid;
  }

  public static void main(String[] args) throws InterruptedException {
    {
      RepeatedTask.Config config = new RepeatedTask.Config();
      config.executeIntervalMs = 1000;
      config.waitStartMs = 500;
      config.waitStopMs = 500;
      config.preprocess = () -> System.out.println("starting...");
      config.task =
          () -> System.out.println("tick @ " + System.currentTimeMillis());
      config.postprocess = () -> System.out.println("stopped.");
      config.stopTrigger = () -> false;

      RepeatedTask task = new RepeatedTask(config);
      task.start();

      Thread.sleep(3000);
      task.stop(); // 会自动释放资源
    }

    {
      RepeatedTask.Config config = new RepeatedTask.Config();
      config.name = "custom-task";
      config.cronExpression = "*/2 * * * * *"; // execute every 2 secs
      config.task =
          () -> System.out.println(Thread.currentThread().getName() + " tick");
      config.stopTrigger = () -> false;

      RepeatedTask task = new RepeatedTask(config);
      task.start();

      Thread.sleep(3000);
      task.stop();
    }
  }
}

package com.chaowen.springboottemplate.base.auxiliry;

import java.util.Arrays;
import java.util.List;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

public class ModuleRunners {

  public static CommandLineRunner getModuleRunner() {
    return new CommandLineRunner() {

      private final org.slf4j.Logger log =
          org.slf4j.LoggerFactory.getLogger(ModuleRunners.class);
      @Autowired
      ApplicationContext ctx;

      @Override
      public void run(String... args) throws Exception {
        if (args.length < 1) {
          log.debug("please specify a module to run");
          return;
        }

        var moduleName = args[0];
        var modules = ctx.getBeansOfType(Module.class);

        var module = modules.get(moduleName);
        if (module == null) {
          log.debug("module not found: {}", moduleName);
          return;
        }

        var moduleArgs = Arrays.asList(
            (args.length > 1) ? args[1].split(",") : new String[]{});

        log.debug("Running Module: <{}> with options: <{}>", moduleName,
            moduleArgs);
        moduleArgs.forEach(arg -> log.debug(" - " + arg));

        module.run(moduleArgs);
      }
    };
  }


  public interface Module {

    void run(List<String> args);
  }
}

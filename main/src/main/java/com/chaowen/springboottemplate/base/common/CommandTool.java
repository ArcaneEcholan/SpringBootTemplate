package com.chaowen.springboottemplate.base.common;

import static com.chaowen.springboottemplate.base.common.Consts.AppProfiles.DEV;
import static com.chaowen.springboottemplate.base.common.Consts.AppProfiles.PROD;
import static com.chaowen.springboottemplate.base.common.Consts.AppProfiles.TEST;

import com.chaowen.springboottemplate.base.auxiliry.Staticed;
import com.chaowen.springboottemplate.base.common.CommandTool.CmdOptions;
import com.chaowen.springboottemplate.base.common.CommandTool.CmdResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

interface Cmd {

  CmdResult executeCommandWithResult(CmdOptions cmdOptions, String... command);

  CmdResult executeCommandWithResult(String... command);

  CmdResult executeCommandWithResult(
      Map<String, String> envs, String... command);

  Boolean executeCommand(String... command);

  Boolean executeCommand(
      Map<String, String> envs, String... command);

}

@Profile({PROD})
@Component
@Slf4j
class CmdRealImpl implements Cmd {

  @Override
  public CmdResult executeCommandWithResult(
      CmdOptions cmdOptions, String... command) {
    return runCmd(cmdOptions, new HashMap<>(), command);
  }

  @Override
  public CmdResult executeCommandWithResult(String... command) {
    return runCmd(new CmdOptions(), new HashMap<>(), command);
  }

  public CmdResult executeCommandWithResult(
      Map<String, String> envs, String... command) {
    return runCmd(new CmdOptions(), envs, command);
  }

  public Boolean executeCommand(String... command) {
    return executeCommandWithResult(new HashMap<>(), command).isSuccess();
  }

  public Boolean executeCommand(
      Map<String, String> envs, String... command) {
    return executeCommandWithResult(envs, command).isSuccess();
  }

  private CmdResult runCmd(
      CmdOptions cmdOptions, Map<String, String> envs, String... command) {
    HelperUtils.reportCmdExe(log, new HashMap<>(), command);

    var tcr = Utils.trycatch(() -> {

      var processBuilder = new ProcessBuilder(command);
      {
        processBuilder.redirectErrorStream(true);
        var environment = processBuilder.environment();
        environment.putAll(envs);
      }

      var process = processBuilder.start();
      var cmdResult = new CmdResult();
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream()))) {
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          out.append(line).append("\n");
        }
        cmdResult.setOutBuf(out.toString());

        if (cmdOptions.logOutput) {
          log.debug("cmd exe output: {}", out);
        }
      }

      int exitCode = process.waitFor();
      cmdResult.setCode(exitCode);
      cmdResult.setSuccess(exitCode == 0);

      if (exitCode != 0) {
        log.debug("cmd exe result code: {}", exitCode);
      } else {
        log.debug("cmd exe result code: {}", exitCode);
      }

      return cmdResult;
    });

    if (tcr.hasEx()) {
      log.error("exe cmd with error: {}", tcr.getMessage());

      var cmdResult = new CmdResult();
      cmdResult.setSuccess(false);
      cmdResult.setCode(1);
      cmdResult.setOutBuf(tcr.getRootMessage());
      return cmdResult;
    }

    return tcr.getValue();
  }

}

@Profile({TEST, DEV})
@Component
class CmdForTest implements Cmd {

  @NotNull
  private static final Logger log =
      Objects.requireNonNull(LoggerFactory.getLogger(CmdForTest.class));

  @Autowired
  CmdExecutionObserveTool cmdExecutionObserveTool;

  @Override
  public CmdResult executeCommandWithResult(
      CmdOptions cmdOptions, String... command) {
    return executeCommandWithResult(command);
  }

  public CmdResult executeCommandWithResult(String... command) {
    HelperUtils.reportCmdExe(log, new HashMap<>(), command);

    cmdExecutionObserveTool.mockExeCommand(command);

    var cmdResult = new CmdResult();
    cmdResult.setCode(0);
    cmdResult.setSuccess(true);
    return cmdResult;
  }

  @Override
  public CmdResult executeCommandWithResult(
      Map<String, String> envs, String... command) {
    HelperUtils.reportCmdExe(log, new HashMap<>(), command);

    cmdExecutionObserveTool.mockExeCommand(command);

    var cmdResult = new CmdResult();
    cmdResult.setCode(0);
    cmdResult.setSuccess(true);
    return cmdResult;
  }


  public Boolean executeCommand(String... command) {
    HelperUtils.reportCmdExe(log, new HashMap<>(), command);

    cmdExecutionObserveTool.mockExeCommand(command);
    return true;
  }

  public Boolean executeCommand(
      Map<String, String> envs, String... command) {
    HelperUtils.reportCmdExe(log, envs, command);

    cmdExecutionObserveTool.mockExeCommand(command);
    return true;
  }
}

@lombok.extern.slf4j.Slf4j
@org.springframework.stereotype.Component
@Staticed
public class CommandTool {

  static Cmd cmd;

  @Data
  public static class CmdResult {

    private String outBuf = "";
    private int code = -1;
    private boolean success = false;
  }

  public static class CmdOptions {

    public boolean forceReal = false;
    public boolean logOutput = true;
  }

  public static CmdResult executeCommandWithResult(
      boolean forceReal, Map<String, String> envs, String... command) {
    if (forceReal) {
      return new CmdRealImpl().executeCommandWithResult(envs, command);
    }

    return executeCommandWithResult(envs, command);
  }

  public static CmdResult executeCommandWithResult(
      CmdOptions cmdOptions, String... command) {
    if (cmdOptions.forceReal) {
      return new CmdRealImpl().executeCommandWithResult(cmdOptions, command);
    }

    return executeCommandWithResult(command);
  }

  public static CmdResult executeCommandWithResult(
      boolean forceReal, String... command) {
    if (forceReal) {
      return new CmdRealImpl().executeCommandWithResult(command);
    }

    return executeCommandWithResult(command);
  }

  public static CmdResult executeCommandWithResult(String... command) {
    return cmd.executeCommandWithResult(command);
  }

  public static CmdResult executeCommandWithResult(
      Map<String, String> envs, String... command) {
    return cmd.executeCommandWithResult(envs, command);
  }

  public static Boolean executeCommand(String... command) {
    return cmd.executeCommand(command);
  }

  public static Boolean executeCommand(
      Map<String, String> envs, String... command) {
    return cmd.executeCommand(envs, command);
  }

}

class HelperUtils {

  static void reportCmdExe(
      Logger log, Map<String, String> envs, String... command) {
    List<String> envList = envs.entrySet().stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.toList());
    log.debug("exe cmd: {}; env: {}", Arrays.toString(command), envList);
  }

}

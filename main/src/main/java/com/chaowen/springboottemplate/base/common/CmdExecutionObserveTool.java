package com.chaowen.springboottemplate.base.common;

import com.chaowen.springboottemplate.base.common.Functions.Runnable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.SneakyThrows;
import lombok.var;
import org.springframework.stereotype.Component;

@Component
public class CmdExecutionObserveTool {

  private static final String TEMP_FILE_PATH = "cmd_mock_execution.log";
  // temp file path for mock command records
  private File file;

  public static void main(String[] args) {
    //CmdExecutionObserveTool tool = new CmdExecutionObserveTool();
    //
    //// illustrate the usage of the Tool
    //new CmdExecutionObserveTool().observeCmdExecution(() -> {
    //  tool.mockExeCommand("ip", "a", "1.1.1.1", "dev", "eth");
    //}, new String[]{"ip", "a", "1.1.1.1", "dev", "eth"});
    //
    //new CmdExecutionObserveTool().observeCmdExecution(() -> {
    //      tool.mockExeCommand("ip", "a", "1.1.1.1", "dev", "eth");
    //      tool.mockExeCommand("ip", "a", "3.3.3.3", "dev", "eth");
    //    }, new String[]{"ip", "a", "1.1.1.1", "dev", "eth"},
    //    new String[]{"ip", "a", "1.1.1.1", "dev", "eth"},
    //    new String[]{"ip", "a", "3.3.3.3", "dev", "eth"});
  }

  static void businessMethod() {
    CmdExecutionObserveTool tool = new CmdExecutionObserveTool();
    if (conditionAlpha()) {
      tool.mockExeCommand("ip", "a", "1.1.1.1", "dev", "eth");
    } else if (conditionBeta()) {
      tool.mockExeCommand("ip", "a", "1.1.1.1", "dev", "eth");
      tool.mockExeCommand("ip", "a", "2.2.2.2", "dev", "eth");
    } else {
      tool.mockExeCommand("ip", "a", "1.1.1.1", "dev", "eth");
      tool.mockExeCommand("ip", "a", "2.2.2.2", "dev", "eth");
      tool.mockExeCommand("ip", "a", "3.3.3.3", "dev", "eth");
    }
  }

  // mock methods for testing conditions
  private static boolean conditionAlpha() {
    return Math.random() > 0.5;
  }

  private static boolean conditionBeta() {
    return Math.random() > 0.5;
  }

  public void mockExeCommand(String... args) {
    file = getTempFileForMockCommandRecord();
    appendToFile(args);
  }

  @SneakyThrows
  public void observeCmdExecution(Runnable func, String... commandSequences) {
    cleanFile();
    func.run();
    checkCmd(commandSequences);
  }

  private File getTempFileForMockCommandRecord() {
    if (file == null || !file.exists()) {
      file = createNewFileForMockCommandRecord();
    }
    return file;
  }

  private File createNewFileForMockCommandRecord() {
    Path path = Paths.get(TEMP_FILE_PATH);
    try {
      Files.deleteIfExists(path); // delete the file if it exists
      return Files.createFile(path).toFile();
    } catch (IOException e) {
      throw new RuntimeException(
          "Failed to create temp file for mock command record", e);
    }
  }

  private void cleanFile() {
    file = getTempFileForMockCommandRecord();
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(""); // clear file content
    } catch (IOException e) {
      throw new RuntimeException("Failed to clean the mock command record file",
          e);
    }
  }

  private void appendToFile(String... args) {
    try (FileWriter writer = new FileWriter(file, true)) {
      writer.write(String.join(" ", args) + System.lineSeparator());
    } catch (IOException e) {
      throw new RuntimeException(
          "Failed to append command to mock command record file", e);
    }
  }

  public List<String> allLines() {
    List<String> fileContent;
    try {
      fileContent = Files.readAllLines(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read mock command record file", e);
    }
    return fileContent;
  }

  private void checkCmd(String... commandSequences) {
    List<String> fileContent;
    try {
      fileContent = Files.readAllLines(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException("failed to read mock command record file", e);
    }

    int minLength = Math.min(commandSequences.length, fileContent.size());
    boolean differenceFound = false;
    int firstDifferenceIndex = -1;
    String expectedCommandAtDiff = null;
    String actualCommandAtDiff = null;

    // check each command up to the minimum length of expected and actual lists
    for (int i = 0; i < minLength; i++) {
      var expectedCommand = String.join(" ", commandSequences[i]);
      var actualCommand = fileContent.get(i);

      if (!expectedCommand.equals(actualCommand)) {
        differenceFound = true;
        firstDifferenceIndex = i;
        expectedCommandAtDiff = expectedCommand;
        actualCommandAtDiff = actualCommand;
        break;
      }
    }

    var errorReport = new StringBuilder();

    if (differenceFound) {
      // if a difference is found within the common length, report it
      errorReport.append("actual command list is not the same as expected:\n\n")
          .append("First difference at index ").append(firstDifferenceIndex)
          .append(":\n").append("expected: ").append(expectedCommandAtDiff)
          .append("\n").append("actual: ").append(actualCommandAtDiff)
          .append("\n\n");
    } else if (commandSequences.length != fileContent.size()) {
      // if no difference found in common length but sizes are different
      errorReport.append(
          "actual command list size does not match expected size:\n\n");
    }

    if (differenceFound || commandSequences.length != fileContent.size()) {
      errorReport.append("Full expected sequence:\n");
      for (int i = 0; i < commandSequences.length; i++) {
        errorReport.append("  ").append(i).append(": ")
            .append(String.join(" ", commandSequences[i])).append("\n");
      }
      errorReport.append("size: ").append(commandSequences.length)
          .append("\n\n");

      errorReport.append("Full actual sequence:\n");
      for (int i = 0; i < fileContent.size(); i++) {
        errorReport.append("  ").append(i).append(": ")
            .append(fileContent.get(i)).append("\n");
      }
      errorReport.append("size: ").append(fileContent.size()).append("\n");

      throw new AssertionError(errorReport.toString());
    }
  }

}

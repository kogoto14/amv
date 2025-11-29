package dev.aulait.amv.arch.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.SystemUtils;

/**
 * @deprecated use {@link dev.aulait.amv.arch.exec.ExecUtils} instead
 */
@Slf4j
@Deprecated
public class ExecUtils {

  public static int exec(String command, Map<String, ?> args) {
    return exec(command, args, Map.of());
  }

  public static int exec(String command, Map<String, ?> args, Map<String, String> environment) {
    ExecResultVo result = execWithResult(command, args, Path.of("."), environment);
    log.info("command stdout: {}, stderr: {}", result.out, result.err);
    return result.exitCode;
  }

  public static ExecResultVo execWithResult(String command, Map<String, ?> args) {
    return execWithResult(command, args, Path.of("."));
  }

  public static ExecResultVo execWithResult(
      String command, Map<String, ?> args, Path workingDirectory) {
    return execWithResult(command, args, workingDirectory, Map.of());
  }

  public static ExecResultVo execWithResult(
      String command, Map<String, ?> args, Path workingDirectory, Map<String, String> environment) {

    CommandLine cmdLine = parse(command);
    cmdLine.setSubstitutionMap(args);

    log.info("Executing command: {}", cmdLine.toString());

    var stdout = new BufferingLogOutputStream();
    var stderr = new BufferingLogOutputStream();
    PumpStreamHandler psh = new PumpStreamHandler(stdout, stderr);

    DefaultExecutor executor =
        DefaultExecutor.builder()
            .setExecuteStreamHandler(psh)
            .setWorkingDirectory(workingDirectory.toFile())
            .get();

    Map<String, String> env = new HashMap<>(System.getenv());
    env.putAll(environment);

    int exitCodeLocal;
    try {
      exitCodeLocal = executor.execute(cmdLine, env);
    } catch (ExecuteException e) {
      String message =
          String.join(
              System.lineSeparator(),
              "stdout:",
              stdout.toString().trim(),
              "stderr:",
              stderr.toString().trim());
      throw new UncheckedIOException(message, e);

      // The feature to get the exit code from the exception might be useful in the future.
      // exitCodeLocal = e.getExitValue();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new ExecResultVo(exitCodeLocal, stdout.toString().trim(), stderr.toString().trim());
  }

  /**
   * Parse command line depending on OS to CommandLine instance.
   *
   * <p>In macOS and Linux, we need to run commands through a interactive / login shell to activate
   * jenv in the command. So we use "zsh -lic <command>" in macOS and "bash -lc <command>" in Linux.
   *
   * <p>In jenv official documentation, it is recommended to configure the shell as a login shell (
   * ~/.zshrc in macOS or ~/.bash_profile in Linux). see:
   * https://github.com/jenv/jenv?tab=readme-ov-file#12-configuring-your-shell
   *
   * @param command the command to parse
   * @return the parsed CommandLine
   */
  private static CommandLine parse(String command) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return new CommandLine("cmd").addArgument("/c").addArgument(command, false);
    } else if (SystemUtils.IS_OS_MAC) {
      return new CommandLine("zsh").addArgument("-lic").addArgument(command, false);
    } else if (SystemUtils.IS_OS_LINUX) {
      return new CommandLine("bash").addArgument("-lc").addArgument(command, false);
    }
    return new CommandLine(command);
  }

  @Slf4j
  static class BufferingLogOutputStream extends LogOutputStream {
    StringBuffer buffer = new StringBuffer();

    @Override
    protected void processLine(String line, int logLevel) {
      log.info(line);
      buffer.append(line).append(System.lineSeparator());
    }

    @Override
    public String toString() {
      return buffer.toString();
    }
  }

  @RequiredArgsConstructor
  @Value
  public static class ExecResultVo {
    private final int exitCode;
    private final String out;
    private final String err;
  }
}

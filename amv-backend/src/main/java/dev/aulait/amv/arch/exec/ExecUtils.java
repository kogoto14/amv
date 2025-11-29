package dev.aulait.amv.arch.exec;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.SystemUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecUtils {

  public static ExecResultVo exec(String command) {
    return exec(command, Map.of());
  }

  public static ExecResultVo exec(String command, Map<String, ?> substitutionMap) {
    return exec(ExecParamVo.builder().command(command).substitutionMap(substitutionMap).build());
  }

  public static ExecResultVo exec(
      String command, Map<String, ?> substitutionMap, Path workingDirectory) {
    return exec(
        ExecParamVo.builder()
            .command(command)
            .substitutionMap(substitutionMap)
            .workingDirectory(workingDirectory)
            .build());
  }

  public static ExecResultVo exec(ExecParamVo param) {
    CommandLine cmdLine = parse(param.getCommand());
    cmdLine.setSubstitutionMap(param.getSubstitutionMap());

    log.info("Executing command: {} at {}", cmdLine.toString(), param.getWorkingDirectory());

    var stdout = new BufferingLogOutputStream(log);
    var stderr = new BufferingLogOutputStream(log);
    PumpStreamHandler psh = new PumpStreamHandler(stdout, stderr);

    DefaultExecutor executor =
        DefaultExecutor.builder()
            .setExecuteStreamHandler(psh)
            .setWorkingDirectory(param.getWorkingDirectory().toFile())
            .get();

    Map<String, String> env = new HashMap<>(System.getenv());
    env.putAll(param.getEnvironment());

    int exitCodeLocal;
    try {
      exitCodeLocal = executor.execute(cmdLine, env);
    } catch (ExecuteException e) {
      String message =
          String.join(
              System.lineSeparator(), "stdout:", stdout.toString(), "stderr:", stderr.toString());
      throw new UncheckedIOException(message, e);

      // The feature to get the exit code from the exception might be useful in the future.
      // exitCodeLocal = e.getExitValue();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return new ExecResultVo(exitCodeLocal, stdout.toString(), stderr.toString());
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
}

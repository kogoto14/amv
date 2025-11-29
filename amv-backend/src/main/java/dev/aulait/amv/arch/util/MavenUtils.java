package dev.aulait.amv.arch.util;

import dev.aulait.amv.arch.exec.ExecResultVo;
import dev.aulait.amv.arch.exec.ExecUtils;
import dev.aulait.amv.arch.file.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.SystemUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MavenUtils {

  private static final String WIN_WRAPPER = "mvnw.cmd";
  private static final String UNIX_WRAPPER = "./mvnw";

  public static void generateClasspathFile(GenerateClassPathParamsVo params) {

    String command =
        "${mvn} ${compile} dependency:build-classpath -T 1C "
            + "-D mdep.outputFile=${classpathFile} "
            + "${projects} ${skipTests} ${fn} ${args}";

    ExecResultVo result =
        ExecUtils.exec(
            command,
            Map.of(
                "mvn",
                decideCommand(params.getProjectDir()),
                "compile",
                params.isCompile() ? "package" : "",
                "classpathFile",
                params.getClasspathFile(),
                "projects",
                params.getProjects().isEmpty()
                    ? ""
                    : "-pl " + String.join(",", params.getProjects()) + " -am",
                "skipTests",
                params.isCompileTests()
                    ? "-D maven.test.skip.exec=true"
                    : "-D maven.test.skip=true",
                "fn",
                params.isFailNever() ? "-fn" : "",
                "args",
                params.getArgs()),
            params.getProjectDir());

    Path buildLogFile = buildLogFile(params.getProjectDir(), result.getExitCode());

    FileUtils.write(buildLogFile, result.getStdout());
  }

  public static Path buildLogFile(Path pomFileDir, int exitCode) {
    return pomFileDir.resolve("target/maven-build-exit-" + exitCode + ".log");
  }

  private static String decideCommand(Path pomFileDir) {
    if (SystemUtils.IS_OS_WINDOWS) {
      if (Files.exists(pomFileDir.resolve(WIN_WRAPPER))) {
        return WIN_WRAPPER;
      }
    } else {
      if (Files.exists(pomFileDir.resolve(UNIX_WRAPPER))) {
        return UNIX_WRAPPER;
      }
    }

    return "mvn";
  }

  @Builder
  @Value
  public static class GenerateClassPathParamsVo {
    private Path projectDir;
    private String classpathFile;
    @Builder.Default private boolean compile = true;
    @Builder.Default private boolean compileTests = false;
    @Builder.Default private List<String> projects = new ArrayList<>();
    @Builder.Default private boolean failNever = false;
    @Builder.Default private String args = "";
  }
}

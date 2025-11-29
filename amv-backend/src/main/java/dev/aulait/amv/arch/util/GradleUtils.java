package dev.aulait.amv.arch.util;

import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.arch.util.ExecUtils.ExecResultVo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.SystemUtils;

public class GradleUtils {

  private static final String WIN_WRAPPER = "gradlew.cmd";
  private static final String UNIX_WRAPPER = "./gradlew";
  private static Pattern pattern = Pattern.compile("sourceCompatibility\\s*=\\s*'(\\d+)'");
  private static final String PRINT_CLASSPATH_TASK =
      """
  task printClasspath {
    doLast {
      println sourceSets.main.output.classesDirs.asPath + System.getProperty("path.separator") + sourceSets.main.compileClasspath.asPath
    }
  }
""";

  public static String detectJavaVersion(String buildScript) {
    var matcher = pattern.matcher(buildScript);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "17";
  }

  public static String generateClasspath(Path projectDir) {
    String buildScript = FileUtils.read(projectDir.resolve("build.gradle"));

    String javaVersion = detectJavaVersion(buildScript);

    JenvUtils.setLocalVersion(projectDir, javaVersion);

    Path buildExtFile =
        FileUtils.write(projectDir.resolve("build_ext.gradle"), buildScript + PRINT_CLASSPATH_TASK);

    String command = decideCommand(projectDir);

    ExecResultVo result =
        ExecUtils.execWithResult(
            command + " -q -b ${buildFile} assemble printClasspath",
            Map.of("buildFile", buildExtFile.getFileName()),
            projectDir);

    return result.getOut();
  }

  static String decideCommand(Path buildFileDir) {
    if (SystemUtils.IS_OS_WINDOWS) {
      if (Files.exists(buildFileDir.resolve(WIN_WRAPPER))) {
        return WIN_WRAPPER;
      }
    } else {
      if (Files.exists(buildFileDir.resolve(UNIX_WRAPPER))) {
        return UNIX_WRAPPER;
      }
    }

    return "gradle";
  }
}

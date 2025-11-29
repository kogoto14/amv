package dev.aulait.amv.arch.util;

import dev.aulait.amv.arch.file.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.SystemUtils;

public class JenvUtils {

  public static String javaHome(String version) {
    Path jenv = Path.of(SystemUtils.USER_HOME).resolve(".jenv/versions/" + version);

    if (Files.exists(jenv)) {
      return jenv.toString();
    }

    return System.getenv("JAVA_HOME_" + version);
  }

  public static Path setLocalVersion(Path projectDir, String version) {
    return FileUtils.write(projectDir.resolve(".java-version"), version);
  }
}

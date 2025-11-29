package dev.aulait.amv.domain.project;

import java.nio.file.Path;
import java.util.List;

public class ProjectDataFactory {

  public static final Path AMV_BACK_DIR =
      Path.of("../../amv-backend/target/amv-home/codebase/amv/amv-backend")
          .resolve("")
          .toAbsolutePath()
          .normalize();

  public static final List<Path> AMV_BACK_SRC_DIRS =
      List.of(nolombokDir(AMV_BACK_DIR), delombokDir(AMV_BACK_DIR));
  public static final Path AMV_BACK_CLASSPATH_FILE = classpathFile(AMV_BACK_DIR);

  private static Path nolombokDir(Path baseDir) {
    return baseDir.resolve("src/main/java_nolombok");
  }

  private static Path delombokDir(Path baseDir) {
    return baseDir.resolve("src/main/java_delombok");
  }

  private static Path classpathFile(Path baseDir) {
    return baseDir.resolve("classpath.txt");
  }
}

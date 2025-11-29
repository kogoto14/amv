package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.exec.ExecUtils;
import dev.aulait.amv.arch.file.FilePathVo;
import dev.aulait.amv.arch.file.FileUtils;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class LombokService {

  private final ProjectLogic projectLogic;

  public void loadProject(Path rootProjectDir, ProjectEntity project) {
    Path projectDir = rootProjectDir.resolve(project.getPath());
    List<Path> assortedPathes = assortAndDelombok(projectDir);

    if (!assortedPathes.isEmpty()) {
      String sourceDirs =
          assortedPathes.stream()
              .map(projectDir::relativize)
              .map(Path::toString)
              .collect(Collectors.joining(","));
      project.setSourceDirs(sourceDirs);
    }
  }

  List<Path> assortAndDelombok(Path projectDir) {
    Path classpathFile = projectLogic.classpathFile(projectDir);

    if (!classpathFile.toFile().exists()) {
      log.info("Classpath file does not exist: {}", classpathFile);
      return List.of();
    }

    String classpath = FileUtils.read(classpathFile);

    if (!Strings.CS.contains(classpath, "lombok")) {
      log.info("lombok not found in classpath: {}", classpath);
      return List.of();
    }

    Path sourceDir = projectDir.resolve("src/main/java");

    if (!Files.exists(sourceDir)) {
      return List.of();
    }

    Path lombokDir = sourceDir.getParent().resolve("java_lombok");
    Path nolombokDir = sourceDir.getParent().resolve("java_nolombok");
    Path delombokDir = sourceDir.getParent().resolve("java_delombok");

    if (Files.exists(lombokDir) && Files.exists(nolombokDir) && Files.exists(delombokDir)) {
      log.info("Lombok assorting and delombok already done: {}", projectDir);
      return List.of(nolombokDir, delombokDir);
    }

    FileUtils.collect(sourceDir, "**/*.java")
        .forEach(javaFile -> assort(javaFile, projectDir, lombokDir, nolombokDir));

    if (FileUtils.walk(lombokDir, "**/*").findAny().isEmpty()) {
      return List.of(nolombokDir);
    }

    Path argFile = createArgFile(projectDir, lombokDir, classpath, delombokDir);

    delombok(projectDir, argFile);

    return Files.exists(delombokDir) ? List.of(nolombokDir, delombokDir) : List.of(nolombokDir);
  }

  void assort(FilePathVo javaFile, Path projectDir, Path lombokDir, Path nolombokDir) {
    String javaSource = FileUtils.read(javaFile.getFilePath());
    Path targetDir =
        Strings.CS.containsAny(
                javaSource, "import lombok.Data;", "import lombok.Value;", "import lombok.Getter;")
            ? lombokDir
            : nolombokDir;

    Path targetFile = targetDir.resolve(javaFile.getRelative());

    FileUtils.copy(javaFile.getFilePath(), targetFile);
  }

  void delombok(Path dir, Path argFile) {
    ExecUtils.exec("java @${argFile}", Map.of("argFile", argFile.toAbsolutePath()), dir);
  }

  Path createArgFile(Path dir, Path lombokDir, String classpath, Path delombokDir) {
    Path argFile = FileUtils.createTempFile(dir, "java-args", ".txt");
    String lombokJar =
        Arrays.stream(classpath.split(java.util.regex.Pattern.quote(File.pathSeparator)))
            .filter(path -> path.contains("lombok") && path.endsWith(".jar"))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "lombok.jar does not found in classpath: " + classpath));
    String content =
        String.join(
            System.lineSeparator(),
            "-jar",
            lombokJar,
            "delombok",
            lombokDir.toAbsolutePath().toString(),
            "--classpath",
            classpath,
            "-d",
            delombokDir.toAbsolutePath().toString());
    FileUtils.write(argFile, content);
    return argFile;
  }
}

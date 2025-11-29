package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.arch.util.MavenUtils;
import dev.aulait.amv.arch.util.MavenUtils.GenerateClassPathParamsVo;
import dev.aulait.amv.domain.project.CodebaseConfigFdo.BuildConfig;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class MavenService {

  private final LombokService lombokService;

  public List<ProjectEntity> loadProject(Path codebaseDir, CodebaseConfigFdo config) {
    List<Path> pomFileDirs = findProjectDirs(codebaseDir, config);

    if (pomFileDirs.isEmpty()) {
      log.info("No Maven projects (pom.xml) found in the codebase: {}", codebaseDir);
      return List.of();
    }

    generateClasspath(pomFileDirs.get(0), config.getBuild());

    String javaVersion = detectJavaVersion(codebaseDir);

    return pomFileDirs.parallelStream()
        .map(pomFileDir -> loadMavenProject(codebaseDir, pomFileDir, javaVersion))
        .peek(project -> lombokService.loadProject(codebaseDir, project))
        .toList();
  }

  ProjectEntity loadMavenProject(Path codebaseDir, Path projectDir, String javaVersion) {
    ProjectEntity project = new ProjectEntity();

    project.setPath(codebaseDir.relativize(projectDir).toString());
    project.setName(projectDir.getFileName().toString());

    if (Files.exists(projectDir.resolve("src/main/java"))) {
      project.setSourceDirs("src/main/java");
    }

    project.setLanguageVersion(javaVersion);

    return project;
  }

  void generateClasspath(Path pomFileDir, BuildConfig config) {
    Path buildLog = MavenUtils.buildLogFile(pomFileDir, 0);

    if (Files.exists(buildLog)) {
      log.info("Skipping classpath generation since build log file already exists: {}", buildLog);
      return;
    }

    // TODO: customize parameters via config
    MavenUtils.generateClasspathFile(
        GenerateClassPathParamsVo.builder()
            .projectDir(pomFileDir)
            .classpathFile(ProjectLogic.CLASSPATH_FILE_NAME)
            .compile(config.isCompile())
            .compileTests(config.isCompileTests())
            .failNever(config.isFailNever())
            .projects(config.getIncludedProjects())
            .args(config.getArgs())
            .build());
  }

  public List<Path> findProjectDirs(Path codebaseDir, CodebaseConfigFdo config) {

    String buildFilePattern =
        Objects.toString(config.getBuild().getBuildFilePattern(), "**pom.xml");

    return FileUtils.walk(codebaseDir, buildFilePattern)
        .map(Path::getParent)
        .sorted(Comparator.comparing(Path::getNameCount))
        .toList();
  }

  String detectJavaVersion(Path dir) {
    Path versionFile = dir.resolve(".java-version");
    if (Files.exists(versionFile)) {
      return FileUtils.read(versionFile).trim();
    }

    // TODO: another detection logic (e.g., pom.xml)

    return null;
  }
}

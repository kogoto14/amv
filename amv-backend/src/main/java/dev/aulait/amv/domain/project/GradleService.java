package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.arch.util.GradleUtils;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class GradleService {

  private final LombokService lombokService;
  private final ProjectLogic projectLogic;

  public List<ProjectEntity> loadProject(Path codebaseDir) {
    List<Path> buildFileDirs =
        // TODO: support Kotlin DSL
        FileUtils.walk(codebaseDir, "build.gradle").map(Path::getParent).toList();

    if (buildFileDirs.isEmpty()) {
      return List.of();
    }

    String classpath = GradleUtils.generateClasspath(codebaseDir);
    FileUtils.write(projectLogic.classpathFile(codebaseDir), classpath);

    return buildFileDirs.stream()
        .map(buildFileDir -> loadGradleProject(codebaseDir, buildFileDir))
        .peek(project -> lombokService.loadProject(codebaseDir, project))
        .toList();
  }

  ProjectEntity loadGradleProject(Path codebaseDir, Path projectDir) {
    ProjectEntity project = new ProjectEntity();

    project.setPath(codebaseDir.relativize(projectDir).toString());
    project.setName(projectDir.getFileName().toString());

    if (Files.exists(projectDir.resolve("src/main/java"))) {
      project.setSourceDirs("src/main/java");
    }

    return project;
  }
}

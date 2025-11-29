package dev.aulait.amv.domain.project;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class ProjectLogic {

  public static final String CLASSPATH_FILE_NAME = "classpath.txt";

  public ProjectVo toVo(
      ProjectEntity entity, BiFunction<CodebaseEntity, String, Path> pathResolver) {

    Path rootDir = pathResolver.apply(entity.getCodebase(), entity.getPath());

    List<Path> sourceDirs = new ArrayList<>();

    if (StringUtils.isNoneEmpty(entity.getSourceDirs())) {
      Stream.of(entity.getSourceDirs().split(","))
          .map(
              sourceDir ->
                  pathResolver.apply(entity.getCodebase(), entity.getPath()).resolve(sourceDir))
          .forEach(sourceDirs::add);
    }

    Path classpathFile =
        pathResolver.apply(entity.getCodebase(), entity.getPath()).resolve(CLASSPATH_FILE_NAME);

    return ProjectVo.builder()
        .entity(entity)
        .rootDir(rootDir)
        .sourceDirs(sourceDirs)
        .classpathFile(classpathFile)
        .build();
  }

  public Path classpathFile(Path projectDir) {
    return projectDir.resolve(CLASSPATH_FILE_NAME);
  }

  public boolean areProjectsInSync(
      List<ProjectEntity> projects, Path codebaseDir, List<Path> projectDirs) {
    if (projects.size() != projectDirs.size()) {
      return false;
    }

    Set<String> projectDirSet =
        projectDirs.stream()
            .map(codebaseDir::relativize)
            .map(Path::toString)
            .collect(Collectors.toSet());

    Set<String> loadedProjectPathSet =
        projects.stream().map(ProjectEntity::getPath).collect(Collectors.toSet());

    return projectDirSet.equals(loadedProjectPathSet);
  }
}

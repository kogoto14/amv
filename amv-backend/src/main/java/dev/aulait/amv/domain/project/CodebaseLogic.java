package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.util.GitUtils;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class CodebaseLogic {

  /** in: codebase.id, out: analysing or not */
  @Setter private Function<String, Boolean> analysisStatusResolver;

  /** in: codebase, out: projectsLoaded or not */
  @Setter private Function<CodebaseEntity, Boolean> projectStatusResolver;

  public Path dir(CodebaseEntity codebase) {
    if (isFilesystem(codebase)) {
      return DirectoryManager.CODEBASE_ROOT.resolve(codebase.getName());
    }
    return GitUtils.extractRootDir(DirectoryManager.CODEBASE_ROOT, codebase.getUrl());
  }

  public Path resolve(CodebaseEntity codebase, String relativePath) {
    return dir(codebase).resolve(relativePath);
  }

  public boolean exists(CodebaseEntity codebase) {
    return dir(codebase).toFile().exists();
  }

  /**
   * Check if the codebase is a filesystem (directory) type (not like Git repo).
   *
   * @param codebase the codebase entity to check
   * @return true if the codebase is a filesystem type, false otherwise
   */
  public boolean isFilesystem(CodebaseEntity codebase) {
    return StringUtils.isEmpty(codebase.getUrl());
  }

  public List<CodebaseAggregate> aggregate(List<ProjectEntity> projects) {
    Map<CodebaseEntity, List<ProjectEntity>> projectsByCodebase =
        projects.stream()
            .filter(project -> project.getCodebase() != null)
            .collect(Collectors.groupingBy(ProjectEntity::getCodebase));

    return projectsByCodebase.entrySet().stream()
        .map(entry -> buildAggregate(entry.getKey(), entry.getValue()))
        .toList();
  }

  public CodebaseAggregate buildAggregate(CodebaseEntity codebase, List<ProjectEntity> projects) {
    return CodebaseAggregate.builder()
        .codebase(codebase)
        .projects(projects)
        .status(collectStatus(codebase))
        .build();
  }

  private CodebaseStatusVo collectStatus(CodebaseEntity codebase) {
    boolean metadataExtracted =
        Files.exists(
            DirectoryManager.getExtractionDir(codebase.getName(), codebase.getCommitHash())
                .resolve(".done"));

    return CodebaseStatusVo.builder()
        .analyzing(analysisStatusResolver.apply(codebase.getId()))
        .checkedOut(exists(codebase))
        .projectsLoaded(projectStatusResolver.apply(codebase))
        .metadataExtracted(metadataExtracted)
        .build();
  }
}

package dev.aulait.amv.domain.process;

import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.domain.extractor.java.ExtractionService;
import dev.aulait.amv.domain.project.CodebaseEntity;
import dev.aulait.amv.domain.project.ProjectEntity;
import dev.aulait.amv.domain.project.ProjectService;
import dev.aulait.amv.domain.project.ProjectVo;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class ProcessService {

  private final ProcessFactory factory;
  private final TypeService typeService;
  private final MethodService methodService;
  private final CrudPointService crudPointService;
  private final ProjectService projectService;
  private final ExtractionService extractionService;

  Path prepareOutDir(CodebaseEntity codebase) {
    return DirectoryManager.createExtractionDir(codebase.getName(), codebase.getCommitHash());
  }

  public Stream<SourceFileAggregate> extractMetadata(
      CodebaseEntity codebase, ProjectEntity project) {

    ProjectVo projectVo = projectService.toVo(project);
    Path outDir = prepareOutDir(codebase);

    return extractionService
        .execute(
            projectVo.getRootDir(),
            projectVo.getSourceDirs(),
            projectVo.getClasspathFile(),
            "**/*.java",
            project.getLanguageVersion(),
            outDir)
        .map(factory::build)
        // TODO: refactor to avoid setting project here
        .peek(aggregate -> aggregate.getSourceFile().setProject(project));
  }

  public void updateRelationAndStats() {
    methodService.updateCallee();
    crudPointService.updateCrudPointDataName();
    methodService.updateStats();
    typeService.updateMethod();
  }

  public void markExtractionDone(CodebaseEntity codebase) {
    Path doneFile =
        DirectoryManager.createExtractionDir(codebase.getName(), codebase.getCommitHash())
            .resolve(".done");
    FileUtils.write(doneFile, LocalDateTime.now().toString());
  }
}

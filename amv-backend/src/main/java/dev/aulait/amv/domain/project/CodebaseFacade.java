package dev.aulait.amv.domain.project;

import com.google.common.base.Stopwatch;
import dev.aulait.amv.arch.async.AsyncExecService;
import dev.aulait.amv.domain.process.ProcessService;
import dev.aulait.amv.domain.process.SourceFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class CodebaseFacade {
  private final AsyncExecService asyncService;
  private final CodebaseService codebaseService;
  private final ProjectService projectService;
  private final ProcessService processService;
  private final SourceFileService sourceFileService;

  public void analyzeAsync(String codebaseId) {
    asyncService.runAsync(codebaseId, () -> analyze(codebaseId));
  }

  @ActivateRequestContext
  public void analyze(String codebaseId) {
    Stopwatch analisisSw = Stopwatch.createStarted();

    CodebaseEntity codebase = codebaseService.load(codebaseId);

    asyncService.notify(codebaseId, "Codebase loaded");

    List<ProjectEntity> projects = projectService.load(codebase);

    asyncService.notify(codebaseId, "Projects loaded");

    Stopwatch extractionSw = Stopwatch.createStarted();
    projects.parallelStream()
        .forEach(
            project ->
                processService.extractMetadata(codebase, project).forEach(sourceFileService::save));
    processService.markExtractionDone(codebase);
    log.info("Extracted source files of {} in {}", codebase.getName(), extractionSw);
    asyncService.notify(codebaseId, "Source files extracted");

    processService.updateRelationAndStats();

    long analysisMs = analisisSw.elapsed(TimeUnit.MILLISECONDS);
    codebaseService.setAnalyzed(codebaseId, analysisMs);

    log.info("Analyzed codebase {} in {}", codebase.getName(), analisisSw);
  }
}

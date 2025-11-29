package dev.aulait.amv.domain.project;

import static dev.aulait.amv.arch.jpa.JpaUtils.findByIdAsResource;

import dev.aulait.amv.arch.async.AsyncExecService;
import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.util.GitUtils;
import dev.aulait.amv.arch.util.SecurityUtils;
import dev.aulait.amv.arch.util.ShortUuidUtils;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchResult;
import dev.aulait.sqb.jpa.JpaSearchQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class CodebaseService {

  private final EntityManager em;
  private final AsyncExecService asyncService;
  private final CodebaseRepository codebaseRepository;
  private final JpaSearchQueryExecutor searchExecutor;
  private final CodebaseLogic logic;
  private final ProjectService projectService;

  public CodebaseEntity find(String id) {
    return findByIdAsResource(codebaseRepository, id);
  }

  public CodebaseAggregate findWithProjects(String id) {
    CodebaseEntity codebase = findByIdAsResource(codebaseRepository, id);

    List<ProjectEntity> projects = projectService.findByCodebase(id);
    return buildAggregate(codebase, projects);
  }

  public List<CodebaseAggregate> findAllWithProjects() {
    Map<CodebaseEntity, List<CodebaseProjection>> projectsByCodebase =
        codebaseRepository.findAllWithProjects().stream()
            .collect(Collectors.groupingBy(CodebaseProjection::getCodebase));

    return projectsByCodebase.entrySet().stream().map(this::toAggregate).toList();
  }

  @Transactional
  public CodebaseEntity save(CodebaseEntity entity) {
    if (entity.getId() == null) {
      entity.setId(ShortUuidUtils.generate());
    } else {
      Optional<CodebaseEntity> codebaseOpt = codebaseRepository.findById(entity.getId());
      codebaseOpt.ifPresent(
          existing -> {
            entity.setCommitHash(existing.getCommitHash());
            entity.setBranch(existing.getBranch());
            entity.setAnalyzedAt(existing.getAnalyzedAt());
            entity.setAnalyzedIn(existing.getAnalyzedIn());
          });
    }
    if (StringUtils.isEmpty(entity.getName())) {
      entity.setName(GitUtils.extractRootDirName(entity.getUrl()));
    }
    entity.setToken(SecurityUtils.encrypt(entity.getToken()));
    return codebaseRepository.save(entity);
  }

  @Transactional
  public void delete(CodebaseEntity entity) {
    CodebaseEntity managedEntity = em.merge(entity);
    codebaseRepository.delete(managedEntity);
  }

  public SearchResult<CodebaseEntity> search(SearchCriteria criteria) {
    return searchExecutor.search(em, criteria);
  }

  public String loadAllAsync(String id) {
    return asyncService.runAsync(() -> loadAll(id));
  }

  @ActivateRequestContext
  int loadAll(String id) {
    CodebaseEntity codebase = load(id);
    projectService.load(codebase);
    return 0;
  }

  @Transactional
  CodebaseEntity load(String id) {
    StopWatch sw = StopWatch.createStarted();

    CodebaseEntity codebase = find(id);

    if (!logic.exists(codebase)) {
      if (StringUtils.isEmpty(codebase.getToken())) {
        GitUtils.gitClone(DirectoryManager.CODEBASE_ROOT, codebase.getUrl());
      } else {
        GitUtils.gitClone(
            DirectoryManager.CODEBASE_ROOT,
            codebase.getUrl(),
            SecurityUtils.decrypt(codebase.getToken()),
            codebase.getId());
      }
    }

    if (StringUtils.isEmpty(codebase.getSite())) {
      if (StringUtils.startsWith(codebase.getUrl(), "http")) {
        codebase.setSite(codebase.getUrl());
      } else {
        String remoteUrl = GitUtils.getRemoteUrl(Path.of(codebase.getUrl()));
        codebase.setSite(remoteUrl);
      }
    }

    Path repoDir = logic.dir(codebase);
    codebase.setCommitHash(GitUtils.getCurrentHash(repoDir));
    codebase.setBranch(GitUtils.getCurrentBranch(repoDir));

    log.info("Loaded codebase {} in {}", codebase.getName(), sw);
    return codebase;
  }

  @Transactional
  public void setAnalyzed(String codebaseId, long durationMs) {
    CodebaseEntity codebase = find(codebaseId);
    codebase.setAnalyzedAt(LocalDateTime.now());
    codebase.setAnalyzedIn(durationMs);
  }

  private CodebaseAggregate toAggregate(Map.Entry<CodebaseEntity, List<CodebaseProjection>> entry) {

    CodebaseEntity codebase = entry.getKey();

    List<ProjectEntity> projects =
        entry.getValue().stream()
            .map(CodebaseProjection::getProject)
            .filter(Objects::nonNull)
            .toList();

    return buildAggregate(codebase, projects);
  }

  private CodebaseAggregate buildAggregate(CodebaseEntity codebase, List<ProjectEntity> projects) {
    return CodebaseAggregate.builder()
        .codebase(codebase)
        .projects(projects)
        .status(buildStatus(codebase))
        .build();
  }

  private CodebaseStatusVo buildStatus(CodebaseEntity codebase) {
    boolean metadataExtracted =
        Files.exists(
            DirectoryManager.getExtractionDir(codebase.getName(), codebase.getCommitHash())
                .resolve(".done"));

    return CodebaseStatusVo.builder()
        .analyzing(asyncService.isRunning(codebase.getId()))
        .checkedOut(logic.exists(codebase))
        .projectsLoaded(projectService.isLoaded(codebase))
        .metadataExtracted(metadataExtracted)
        .build();
  }
}

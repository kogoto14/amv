package dev.aulait.amv.domain.project;

import static dev.aulait.amv.arch.jpa.JpaUtils.findByIdAsResource;

import dev.aulait.amv.arch.util.ShortUuidUtils;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchResult;
import dev.aulait.sqb.jpa.JpaSearchQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

  private final EntityManager em;

  private final ProjectRepository projectRepository;

  private final JpaSearchQueryExecutor searchExecutor;

  private final MavenService mavenService;

  private final GradleService gradleService;

  private final CodebaseLogic codebaseLogic;

  private final ProjectLogic logic;

  public ProjectEntity find(String id) {
    return findByIdAsResource(projectRepository, id);
  }

  public List<ProjectEntity> findByCodebase(String codebaseId) {
    return projectRepository.findByCodebase_IdOrIdIn(codebaseId, List.of());
  }

  public List<ProjectVo> findByCodebase(String codebaseId, List<String> projectIds) {
    List<ProjectEntity> entities =
        projectRepository.findByCodebase_IdOrIdIn(codebaseId, projectIds);
    return entities.stream().map(project -> logic.toVo(project, codebaseLogic::resolve)).toList();
  }

  // TODO: refactor
  public ProjectVo toVo(ProjectEntity entity) {
    return logic.toVo(entity, codebaseLogic::resolve);
  }

  public List<ProjectEntity> findAllWithCodebase() {
    return projectRepository.findAllWithCodebase();
  }

  @Transactional
  public ProjectEntity save(ProjectEntity entity) {
    return projectRepository.save(entity);
  }

  @Transactional
  public void delete(ProjectEntity entity) {
    ProjectEntity managedEntity = em.merge(entity);
    projectRepository.delete(managedEntity);
  }

  public SearchResult<ProjectEntity> search(SearchCriteria criteria) {
    return searchExecutor.search(em, criteria);
  }

  public List<ProjectEntity> load(CodebaseEntity codebase) {
    if (isLoaded(codebase)) {
      log.info("Codebase {} is already loaded", codebase.getName());
      return findByCodebase(codebase.getId());
    }

    StopWatch sw = StopWatch.createStarted();

    Path rootProjectDir = codebaseLogic.dir(codebase);

    // TODO: Consider appropriate timing to load config
    CodebaseConfigFdo config = CodebaseConfigFdo.load(rootProjectDir);

    List<ProjectEntity> projects = new ArrayList<>();
    projects.addAll(mavenService.loadProject(rootProjectDir, config));
    projects.addAll(gradleService.loadProject(rootProjectDir));

    var result =
        projects.stream()
            .peek(
                project -> {
                  project.setId(ShortUuidUtils.generate());
                  project.setCodebase(codebase);
                })
            .peek(this::save)
            .toList();

    log.info("Loaded {} projects for codebase {} in {}", result.size(), codebase.getName(), sw);
    return result;
  }

  public boolean isLoaded(CodebaseEntity codebase) {
    Path rootProjectDir = codebaseLogic.dir(codebase);

    if (Files.notExists(rootProjectDir)) {
      return false;
    }

    List<ProjectEntity> existingProjects = findByCodebase(codebase.getId());

    if (existingProjects.isEmpty()) {
      return false;
    }

    CodebaseConfigFdo config = CodebaseConfigFdo.load(rootProjectDir);

    List<Path> projectDirs = new ArrayList<>();
    projectDirs.addAll(mavenService.findProjectDirs(rootProjectDir, config));
    // TODO: Enable Gradle project detection
    // projectDirs.addAll(gradleService.findProjectDirs(rootProjectDir));

    return logic.areProjectsInSync(existingProjects, rootProjectDir, projectDirs);
  }
}

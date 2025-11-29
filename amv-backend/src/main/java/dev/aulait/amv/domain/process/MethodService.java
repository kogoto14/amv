package dev.aulait.amv.domain.process;

import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchResult;
import dev.aulait.sqb.jpa.JpaSearchQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class MethodService {

  private final MethodRepository repository;

  private final MethodCallRepository methodCallRepository;

  private final SourceFileRepository sourceFileRepository;

  private final SourceFileLogic sourceFileLogic;

  private final EntityManager em;

  private final JpaSearchQueryExecutor searchExecutor;

  @Transactional
  public long updateCallee() {
    LocalDateTime now = LocalDateTime.now();
    return repository.updateInterfaceCount(em, "annonymous", now)
        + methodCallRepository.updateInterfaceCallee(em, "annonymous", now)
        + methodCallRepository.updateCallee(em, "annonymous", now);
  }

  @Transactional
  public long updateStats() {
    LocalDateTime now = LocalDateTime.now();
    return repository.updateCallCount(em, "annonymous", now);
  }

  /**
   * @param typeIds
   * @return key: typeId, value: URL of the source file
   */
  public Map<String, String> resolveUrl(Set<String> typeIds) {
    return sourceFileRepository.findLinkByTypeIds(typeIds).stream()
        .collect(Collectors.toMap(SourceFileLinkProjection::getTypeId, sourceFileLogic::buildUrl));
  }

  public SearchResult<MethodEntity> search(SearchCriteria criteria) {
    return searchExecutor.search(em, criteria);
  }
}

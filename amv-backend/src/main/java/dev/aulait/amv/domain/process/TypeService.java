package dev.aulait.amv.domain.process;

import dev.aulait.amv.arch.exception.ResourceNotFoundException;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchResult;
import dev.aulait.sqb.jpa.JpaSearchQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class TypeService {

  private final EntityManager em;

  private final TypeRepository typeRepository;

  private final JpaSearchQueryExecutor searchExecutor;

  public TypeEntity find(String idOrQualifiedName) {
    return typeRepository
        .findByIdOrQualifiedName(idOrQualifiedName, idOrQualifiedName)
        .orElseThrow(() -> new ResourceNotFoundException(idOrQualifiedName));
  }

  @Transactional
  public TypeEntity save(TypeEntity entity) {
    return typeRepository.save(entity);
  }

  @Transactional
  public long updateMethod() {
    LocalDateTime now = LocalDateTime.now();
    return typeRepository.updateMethod(em, "annonymous", now);
  }

  @Transactional
  public void delete(TypeEntity entity) {
    TypeEntity managedEntity = em.merge(entity);
    typeRepository.delete(managedEntity);
  }

  public SearchResult<TypeEntity> search(SearchCriteria criteria) {
    return searchExecutor.search(em, criteria);
  }
}

package dev.aulait.amv.domain.process;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CrudPointService {

  private final CrudPointRepository crudPointRepository;

  private final EntityManager em;

  @Transactional
  public long updateCrudPointDataName() {
    return crudPointRepository.updateDataName(em, "annonymous", LocalDateTime.now());
  }
}

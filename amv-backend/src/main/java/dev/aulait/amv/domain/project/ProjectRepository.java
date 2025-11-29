package dev.aulait.amv.domain.project;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {
  @Query(
      """
      SELECT p FROM ProjectEntity p
      WHERE p.codebase.id = :codebaseId OR p.id IN :ids
      """)
  List<ProjectEntity> findByCodebase_IdOrIdIn(String codebaseId, List<String> ids);

  @Query(
      """
      SELECT p FROM ProjectEntity p
      JOIN FETCH p.codebase
      """)
  List<ProjectEntity> findAllWithCodebase();
}

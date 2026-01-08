package dev.aulait.amv.domain.project;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CodebaseRepository extends JpaRepository<CodebaseEntity, String> {

  static final String FIND_BY_ID_WITH_PROJECTS =
      "SELECT c AS codebase, p AS project FROM CodebaseEntity c LEFT JOIN FETCH ProjectEntity p ON"
          + " c.id = p.codebase.id";

  @Query(FIND_BY_ID_WITH_PROJECTS + " WHERE c.id = :id")
  List<CodebaseProjection> findWithProjectsById(@Param("id") String id);

  @Query(FIND_BY_ID_WITH_PROJECTS + " WHERE c.name = :name")
  List<CodebaseProjection> findWithProjectsByName(@Param("name") String name);

  @Query(FIND_BY_ID_WITH_PROJECTS)
  List<CodebaseProjection> findAllWithProjects();
}

package dev.aulait.amv.domain.process;

import dev.aulait.amv.domain.project.SourceFileEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SourceFileRepository extends JpaRepository<SourceFileEntity, String> {

  // @Query(
  //   """
  //     SELECT
  //       c.url AS codebaseUrl,
  //       p.path AS projectPath,
  //       s.path AS sourceFilePath,
  //       t.id  AS typeId
  //     FROM SourceFileEntity s
  //     JOIN s.project p
  //     JOIN p.codebase c
  //     JOIN TypeEntity t ON t.sourceFile.id = s.id
  //     WHERE t.id IN :typeIds
  //   """)
  // Note: Use single-line string literal because
  // multi-line string literals make results null values
  // https://stackoverflow.com/questions/79700811/spring-data-jpa-interface-projection-returns-null-values
  @Query(
      "SELECT c.site AS codebaseSite, "
          + "p.path AS projectPath, "
          + "s.path AS sourceFilePath, "
          + "t.id  AS typeId "
          + "FROM SourceFileEntity s "
          + "JOIN s.project p "
          + "JOIN p.codebase c "
          + "JOIN TypeEntity t ON t.sourceFile.id = s.id "
          + "WHERE t.id IN :typeIds")
  List<SourceFileLinkProjection> findLinkByTypeIds(Set<String> typeIds);
}

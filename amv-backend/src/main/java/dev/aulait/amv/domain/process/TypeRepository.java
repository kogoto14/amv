package dev.aulait.amv.domain.process;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TypeRepository extends JpaRepository<TypeEntity, String> {

  Optional<TypeEntity> findByQualifiedName(String qualifiedName);

  List<TypeEntity> findByQualifiedNameIn(Collection<String> qualifiedNames);

  Optional<TypeEntity> findByIdOrQualifiedName(String id, String qualifiedName);

  @Query("SELECT sf.path FROM TypeEntity t JOIN t.sourceFile sf WHERE t.id = :typeId")
  String findSrcPathById(String typeId);

  static final String UPDATE_METHOD_POSTGRES =
      """
        UPDATE type
        SET method_cnt = sub.method_cnt,
          unsolved_cnt = sub.unsolved_cnt,
          unsolved_rate = ROUND(
              1.0 * sub.unsolved_cnt / (sub.method_cnt + sub.method_call_cnt)
              , 3
          ),
          updated_by = :updateBy,
          updated_at = :updateAt
        FROM (
            SELECT type_id,
              COUNT(1) AS method_cnt,
              COALESCE(SUM(method_call_cnt), 0) AS method_call_cnt,
              COUNT(fallback_signature)
                + COALESCE(SUM(unsolved_method_call_cnt), 0) AS unsolved_cnt
            FROM method
            GROUP BY type_id
          ) AS sub
        WHERE type.id = sub.type_id
      """;

  default long updateMethod(EntityManager em, String updateBy, LocalDateTime updateAt) {
    return em.createNativeQuery(UPDATE_METHOD_POSTGRES)
        .setParameter("updateBy", updateBy)
        .setParameter("updateAt", updateAt)
        .executeUpdate();
  }
}

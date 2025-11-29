package dev.aulait.amv.domain.process;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrudPointRepository extends JpaRepository<CrudPointEntity, String> {

  static final String UPDATE_DATA_NAME_POSTGRES =
      """
      UPDATE crud_point
        SET data_name = t.data_name,
          updated_by = :updateBy,
          updated_at = :updateAt
      FROM type AS t
      WHERE t.qualified_name = crud_point.type
      """;

  default long updateDataName(EntityManager em, String updateBy, LocalDateTime updateAt) {
    return em.createNativeQuery(UPDATE_DATA_NAME_POSTGRES)
        .setParameter("updateBy", updateBy)
        .setParameter("updateAt", updateAt)
        .executeUpdate();
  }
}

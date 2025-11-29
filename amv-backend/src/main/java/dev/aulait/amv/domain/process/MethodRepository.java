package dev.aulait.amv.domain.process;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MethodRepository extends JpaRepository<MethodEntity, MethodEntityId> {

  Optional<MethodEntity> findByQualifiedSignature(String qualifiedSignature);

  List<MethodEntity> findByQualifiedSignatureLike(String signaturePattern, Pageable pageable);

  @Query(
      "SELECT m AS method, mc AS methodCall FROM MethodEntity m JOIN FETCH m.methodCalls mc WHERE"
          + " mc.method.id = :calleeId")
  List<MethodCallProjection> findByCalleeId(MethodEntityId calleeId);

  List<MethodEntity> findByEntryPointIsNotNull(Pageable pageable);

  static final String UPDATE_INTERFACE_COUNT_POSTGRES =
      """
        UPDATE method
        SET interface_signature_cnt = sub.cnt,
          updated_by = :updateBy,
          updated_at = :updateAt
        FROM
          (SELECT interface_signature, COUNT(interface_signature) AS cnt
           FROM method
           WHERE interface_signature IS NOT NULL
           GROUP BY interface_signature) AS sub
        WHERE
          method.interface_signature = sub.interface_signature
      """;

  default long updateInterfaceCount(EntityManager em, String updateBy, LocalDateTime updateAt) {
    return em.createNativeQuery(UPDATE_INTERFACE_COUNT_POSTGRES)
        .setParameter("updateBy", updateBy)
        .setParameter("updateAt", updateAt)
        .executeUpdate();
  }

  static final String UPDATE_UNSOLVED_COUNT_POSTGRES =
      """
          UPDATE method
          SET method_call_cnt = sub.cnt,
            unsolved_method_call_cnt = sub.unsolved_cnt,
            updated_by = :updateBy,
            updated_at = :updateAt
          FROM (
              SELECT type_id,
                method_seq_no,
                COUNT(1) AS cnt,
                COUNT(fallback_signature) AS unsolved_cnt
              FROM method_call
              GROUP BY type_id,
                method_seq_no
            ) AS sub
          WHERE method.type_id = sub.type_id
            AND method.seq_no = sub.method_seq_no
      """;

  default long updateCallCount(EntityManager em, String updateBy, LocalDateTime updateAt) {
    return em.createNativeQuery(UPDATE_UNSOLVED_COUNT_POSTGRES)
        .setParameter("updateBy", updateBy)
        .setParameter("updateAt", updateAt)
        .executeUpdate();
  }
}

package dev.aulait.amv.domain.process;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MethodCallRepository extends JpaRepository<MethodCallEntity, MethodCallEntityId> {

  static final String UPDATE_CALLEE_POSTGRES =
      """
      UPDATE method_call
      SET callee_type_id = m.type_id,
        callee_seq_no = m.seq_no,
        updated_by = :updateBy,
        updated_at = :updateAt
      FROM method AS m
      WHERE m.qualified_signature = method_call.qualified_signature
      """;

  default long updateCallee(EntityManager em, String updateBy, LocalDateTime updateAt) {
    return em.createNativeQuery(UPDATE_CALLEE_POSTGRES)
        .setParameter("updateBy", updateBy)
        .setParameter("updateAt", updateAt)
        .executeUpdate();
  }

  static final String UPDATE_INTERFACE_CALLEE_POSTGRES =
      """
        UPDATE method_call
        SET qualified_signature = method.qualified_signature,
          updated_by = :updateBy,
          updated_at = :updateAt
        FROM
          method
        WHERE
          method_call.interface_signature = method.interface_signature
          AND method.interface_signature_cnt = 1
      """;

  default long updateInterfaceCallee(EntityManager em, String updateBy, LocalDateTime updateAt) {
    return em.createNativeQuery(UPDATE_INTERFACE_CALLEE_POSTGRES)
        .setParameter("updateBy", updateBy)
        .setParameter("updateAt", updateAt)
        .executeUpdate();
  }

  @Query(
      """
      SELECT m FROM MethodCallEntity m
      WHERE m.id.typeId = :callerTypeId
        AND m.id.methodSeqNo = :callerMethodSeqNo
        AND m.callerSeqNo = :callerSeqNo
      ORDER BY
        m.lineNo, m.id.seqNo ASC
      """)
  List<MethodCallEntity> findByCallerId(
      String callerTypeId, Integer callerMethodSeqNo, Integer callerSeqNo);

  default List<MethodCallEntity> findByCallerId(MethodCallEntityId callerId) {
    return findByCallerId(callerId.getTypeId(), callerId.getMethodSeqNo(), callerId.getSeqNo());
  }
}

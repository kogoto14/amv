package dev.aulait.amv.domain.process;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import javax.annotation.processing.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Generated("dev.aulait.jeg:jpa-entity-generator")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "method_call")
public class MethodCallEntity extends dev.aulait.amv.arch.jpa.BaseEntity
    implements java.io.Serializable {

  @EqualsAndHashCode.Include @EmbeddedId private MethodCallEntityId id;

  @Column(name = "qualified_signature")
  private String qualifiedSignature;

  @Column(name = "fallback_signature")
  private String fallbackSignature;

  @Column(name = "interface_signature")
  private String interfaceSignature;

  @Column(name = "unsolved_reason")
  private String unsolvedReason;

  @Column(name = "argument_types")
  private String argumentTypes;

  @Column(name = "line_no")
  private Integer lineNo;

  @Column(name = "caller_seq_no")
  private Integer callerSeqNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(name = "callee_type_id", referencedColumnName = "type_id"),
    @JoinColumn(name = "callee_seq_no", referencedColumnName = "seq_no")
  })
  private MethodEntity method;
}

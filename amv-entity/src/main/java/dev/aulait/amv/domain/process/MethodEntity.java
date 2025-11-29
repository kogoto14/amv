package dev.aulait.amv.domain.process;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "method")
public class MethodEntity extends dev.aulait.amv.arch.jpa.BaseEntity
    implements java.io.Serializable {

  @EqualsAndHashCode.Include @EmbeddedId private MethodEntityId id;

  @Column(name = "name")
  private String name;

  @Column(name = "qualified_signature")
  private String qualifiedSignature;

  @Column(name = "fallback_signature")
  private String fallbackSignature;

  @Column(name = "interface_signature")
  private String interfaceSignature;

  @Column(name = "interface_signature_cnt")
  private Integer interfaceSignatureCnt;

  @Column(name = "unsolved_reason")
  private String unsolvedReason;

  @Column(name = "line_no")
  private Integer lineNo;

  @Column(name = "return_type")
  private String returnType;

  @Column(name = "annotations")
  private String annotations;

  @Column(name = "method_call_cnt")
  private Integer methodCallCnt;

  @Column(name = "unsolved_method_call_cnt")
  private Integer unsolvedMethodCallCnt;

  @OneToOne(fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private EntryPointEntity entryPoint;

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(
        name = "type_id",
        referencedColumnName = "type_id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "method_seq_no",
        referencedColumnName = "seq_no",
        insertable = false,
        updatable = false)
  })
  private Set<CrudPointEntity> crudPoints = new HashSet<>();

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(
        name = "type_id",
        referencedColumnName = "type_id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "method_seq_no",
        referencedColumnName = "seq_no",
        insertable = false,
        updatable = false)
  })
  private Set<MethodCallEntity> methodCalls = new HashSet<>();

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(
        name = "type_id",
        referencedColumnName = "type_id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "method_seq_no",
        referencedColumnName = "seq_no",
        insertable = false,
        updatable = false)
  })
  private Set<MethodParamEntity> methodParams = new HashSet<>();
}

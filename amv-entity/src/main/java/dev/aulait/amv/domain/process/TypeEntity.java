package dev.aulait.amv.domain.process;

import dev.aulait.amv.domain.project.SourceFileEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "type")
public class TypeEntity extends dev.aulait.amv.arch.jpa.BaseEntity implements java.io.Serializable {

  @EqualsAndHashCode.Include
  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "qualified_name")
  private String qualifiedName;

  @Column(name = "kind")
  private String kind;

  @Column(name = "annotations")
  private String annotations;

  @Column(name = "data_kind")
  private String dataKind;

  @Column(name = "data_name")
  private String dataName;

  @Column(name = "method_cnt")
  private Integer methodCnt;

  @Column(name = "unsolved_cnt")
  private Integer unsolvedCnt;

  @Column(name = "unsolved_rate")
  private java.math.BigDecimal unsolvedRate;

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Set<FieldEntity> fields = new HashSet<>();

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Set<MethodEntity> methods = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_file_id", referencedColumnName = "id")
  private SourceFileEntity sourceFile;
}

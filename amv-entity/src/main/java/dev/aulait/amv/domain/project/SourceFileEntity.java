package dev.aulait.amv.domain.project;

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
@Table(name = "source_file")
public class SourceFileEntity extends dev.aulait.amv.arch.jpa.BaseEntity
    implements java.io.Serializable {

  @EqualsAndHashCode.Include
  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "path")
  private String path;

  @Column(name = "namespace")
  private String namespace;

  @Column(name = "content")
  private String content;

  @Builder.Default
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "source_file_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private Set<ReferenceEntity> references = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", referencedColumnName = "id")
  private ProjectEntity project;
}

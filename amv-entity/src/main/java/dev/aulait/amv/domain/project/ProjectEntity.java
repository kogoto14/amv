package dev.aulait.amv.domain.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "project")
public class ProjectEntity extends dev.aulait.amv.arch.jpa.BaseEntity
    implements java.io.Serializable {

  @EqualsAndHashCode.Include
  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "path")
  private String path;

  @Column(name = "source_dirs")
  private String sourceDirs;

  @Column(name = "language_version")
  private String languageVersion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "codebase_id", referencedColumnName = "id")
  private CodebaseEntity codebase;
}

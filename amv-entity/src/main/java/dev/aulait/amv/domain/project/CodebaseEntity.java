package dev.aulait.amv.domain.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "codebase")
public class CodebaseEntity extends dev.aulait.amv.arch.jpa.BaseEntity
    implements java.io.Serializable {

  @EqualsAndHashCode.Include
  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "url")
  private String url;

  @Column(name = "site")
  private String site;

  @Column(name = "token")
  private String token;

  @Column(name = "commit_hash")
  private String commitHash;

  @Column(name = "branch")
  private String branch;

  @Column(name = "analyzed_at")
  private LocalDateTime analyzedAt;

  @Column(name = "analyzed_in")
  private Long analyzedIn;
}

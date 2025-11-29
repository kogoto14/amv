package dev.aulait.amv.domain.process;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "method_param")
public class MethodParamEntity extends dev.aulait.amv.arch.jpa.BaseEntity
    implements java.io.Serializable {

  @EqualsAndHashCode.Include @EmbeddedId private MethodParamEntityId id;

  @Column(name = "name")
  private String name;

  @Column(name = "type")
  private String type;
}

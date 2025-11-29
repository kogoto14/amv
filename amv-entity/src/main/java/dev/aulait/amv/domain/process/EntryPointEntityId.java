package dev.aulait.amv.domain.process;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import javax.annotation.processing.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Generated("dev.aulait.jeg:jpa-entity-generator")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class EntryPointEntityId implements java.io.Serializable {

  @Column(name = "type_id")
  private String typeId;

  @Column(name = "method_seq_no")
  private Integer methodSeqNo;
}

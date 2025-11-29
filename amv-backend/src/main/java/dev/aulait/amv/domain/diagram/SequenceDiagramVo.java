package dev.aulait.amv.domain.diagram;

import java.util.Set;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class SequenceDiagramVo {
  private DiagramVo diagram;
  private Set<String> participantStereotypes;
  private Set<String> paramOrReturnTypes;

  public static SequenceDiagramVo empty() {
    return SequenceDiagramVo.builder()
        .diagram(DiagramVo.empty())
        .participantStereotypes(Set.of())
        .paramOrReturnTypes(Set.of())
        .build();
  }
}

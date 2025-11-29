package dev.aulait.amv.domain.document;

import dev.aulait.amv.domain.diagram.DiagramVo;
import dev.aulait.amv.domain.diagram.SequenceDiagramVo;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class InteractionDocumentVo {
  private SequenceDiagramVo sequenceDiagram;
  private DiagramVo classDiagram;
}

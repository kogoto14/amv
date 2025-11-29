package dev.aulait.amv.interfaces.document;

import dev.aulait.amv.interfaces.diagram.DiagramDto;
import dev.aulait.amv.interfaces.diagram.SequenceDiagramDto;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class InteractionResponseDto {
  @Schema(required = true)
  private SequenceDiagramDto sequenceDiagram;

  @Schema(required = true)
  private DiagramDto classDiagram;
}

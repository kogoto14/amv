package dev.aulait.amv.interfaces.diagram;

import java.util.List;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class SequenceDiagramDto {
  @Schema(required = true)
  private DiagramDto diagram;

  @Schema(required = true)
  private List<String> participantStereotypes;
}

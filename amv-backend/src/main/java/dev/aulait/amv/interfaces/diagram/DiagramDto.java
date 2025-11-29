package dev.aulait.amv.interfaces.diagram;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class DiagramDto {
  @Schema(required = true)
  private String text;

  @Schema(required = true)
  private String image;
}

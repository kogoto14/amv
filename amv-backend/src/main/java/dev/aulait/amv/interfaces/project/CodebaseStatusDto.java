package dev.aulait.amv.interfaces.project;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class CodebaseStatusDto {

  @Schema(required = true)
  private boolean analyzing;

  @Schema(required = true)
  private boolean checkedOut;

  @Schema(required = true)
  private boolean projectsLoaded;

  @Schema(required = true)
  private boolean metadataExtracted;
}

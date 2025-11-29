package dev.aulait.amv.interfaces.process;

import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class CrudPointDto {
  @Schema(required = true)
  private String dataName;

  @Schema(required = true)
  private String type;

  @Schema(required = true)
  private String crud;
}

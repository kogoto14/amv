package dev.aulait.amv.interfaces.document;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
public class InteractionDocumentCriteriaDto {
  @Schema(required = true)
  private String qualifiedSignature;

  @Schema(required = true)
  private List<String> participableStereotypes = new ArrayList<>();
}

package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.interfaces.process.MethodDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Builder
@Data
public class CallTreeElementDto {
  /** true: call, false: called */
  @Schema(required = true)
  private boolean call;

  @Schema(required = true)
  private int depth;

  @Schema(required = true)
  private MethodDto method;

  @Schema(required = true)
  private String urlAt;

  @Schema(required = true)
  private int lineNoAt;

  @Schema(required = true)
  @Builder.Default
  private List<String> elementTags = new ArrayList<>();

  @Schema(required = true)
  @Builder.Default
  private List<String> typeTags = new ArrayList<>();

  @Schema(required = true)
  @Builder.Default
  private List<String> methodTags = new ArrayList<>();
}

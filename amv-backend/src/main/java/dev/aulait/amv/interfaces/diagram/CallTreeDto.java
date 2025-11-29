package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.interfaces.process.MethodDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Builder
@Data
public class CallTreeDto {
  @Schema(required = true)
  private MethodDto method;

  @Schema(required = true)
  private List<CallTreeElementDto> callTree;

  @Schema(required = true)
  private List<CallTreeElementDto> calledTree;
}

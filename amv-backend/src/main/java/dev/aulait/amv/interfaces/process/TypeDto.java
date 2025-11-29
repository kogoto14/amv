package dev.aulait.amv.interfaces.process;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeDto {

  @Schema(required = true, readOnly = true)
  private String id;

  @Schema(required = true)
  private String name;

  private String qualifiedName;

  private String kind;

  private String annotations;

  private Integer unsolvedCnt;

  private java.math.BigDecimal unsolvedRate;

  @Schema(required = true)
  @Builder.Default
  private List<FieldDto> fields = new ArrayList<>();

  @Schema(required = true)
  @Builder.Default
  private List<MethodDto> methods = new ArrayList<>();

  @Schema(required = true)
  private SourceFileDto sourceFile;

  @Schema(required = true, readOnly = true)
  private Long version;
}

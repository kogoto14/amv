package dev.aulait.amv.interfaces.project;

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
public class CodebaseDto {

  @Schema(required = true)
  private String id;

  private String name;

  private String url;

  private String token;

  @Schema(readOnly = true)
  private String branch;

  @Schema(readOnly = true)
  private String commitHash;

  @Schema(readOnly = true)
  private Long analyzedIn;

  @Schema(required = true)
  @Builder.Default
  private List<ProjectDto> projects = new ArrayList<>();

  @Schema(readOnly = true)
  private Long version;

  @Schema(required = true)
  @Builder.Default
  private CodebaseStatusDto status = new CodebaseStatusDto();
}

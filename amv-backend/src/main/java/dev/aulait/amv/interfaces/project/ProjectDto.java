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
public class ProjectDto {

  @Schema(required = true)
  private String id;

  private String name;

  private String path;

  // private String sourceDirs;

  @Builder.Default private List<String> sourceDirs = new ArrayList<>();
  private String classpathFile;

  @Schema(required = true)
  private Long version;
}

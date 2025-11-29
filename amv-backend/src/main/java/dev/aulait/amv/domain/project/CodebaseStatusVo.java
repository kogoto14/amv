package dev.aulait.amv.domain.project;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CodebaseStatusVo {
  private boolean analyzing;
  private boolean checkedOut;
  private boolean projectsLoaded;
  private boolean metadataExtracted;
}

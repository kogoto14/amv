package dev.aulait.amv.domain.project;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CodebaseAggregate {
  private CodebaseEntity codebase;
  @Builder.Default private List<ProjectEntity> projects = new ArrayList<>();
  private CodebaseStatusVo status;
}

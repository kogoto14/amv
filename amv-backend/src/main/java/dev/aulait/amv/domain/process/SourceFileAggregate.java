package dev.aulait.amv.domain.process;

import dev.aulait.amv.domain.project.SourceFileEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SourceFileAggregate {
  private SourceFileEntity sourceFile;
  private List<TypeEntity> types = new ArrayList<>();
}

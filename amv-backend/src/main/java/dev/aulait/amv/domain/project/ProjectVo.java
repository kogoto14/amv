package dev.aulait.amv.domain.project;

import java.nio.file.Path;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ProjectVo {
  private ProjectEntity entity;
  private Path rootDir;
  private List<Path> sourceDirs;
  private Path classpathFile;
}

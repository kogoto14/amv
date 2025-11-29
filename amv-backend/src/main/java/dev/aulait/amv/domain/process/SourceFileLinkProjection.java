package dev.aulait.amv.domain.process;

public interface SourceFileLinkProjection {
  String getCodebaseSite();

  String getProjectPath();

  String getSourceFilePath();

  String getTypeId();
}

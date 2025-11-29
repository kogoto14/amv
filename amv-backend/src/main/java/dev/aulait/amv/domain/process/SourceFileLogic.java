package dev.aulait.amv.domain.process;

import dev.aulait.amv.domain.project.SourceFileEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.function.Function;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class SourceFileLogic {

  @Setter private Function<String, Optional<SourceFileEntity>> findSrcFileByTypeId;

  public String findUrl(String typeId, int lineNo) {
    if (typeId == null) {
      return "";
    }

    Optional<SourceFileEntity> srcFileOpt = findSrcFileByTypeId.apply(typeId);

    if (srcFileOpt.isEmpty()) {
      return "";
    }

    return findUrl(srcFileOpt.get()) + "#L" + lineNo;
  }

  private String findUrl(SourceFileEntity sourceFile) {

    String codeBaseSite = sourceFile.getProject().getCodebase().getSite();
    codeBaseSite = StringUtils.replace(codeBaseSite, "/git/", "/").replace(".git", "/blob/master/");

    String projectPath = sourceFile.getProject().getPath();

    return codeBaseSite + projectPath + "/" + sourceFile.getPath();
  }

  public String buildUrl(SourceFileLinkProjection link) {
    String codeBaseSite = link.getCodebaseSite();
    codeBaseSite = StringUtils.replace(codeBaseSite, "/git/", "/").replace(".git", "/blob/master/");

    String projectPath = link.getProjectPath();
    String sourceFilePath = link.getSourceFilePath();

    return codeBaseSite + projectPath + "/" + sourceFilePath;
  }
}

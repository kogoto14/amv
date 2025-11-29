package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.util.GitUtils;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Path;

@ApplicationScoped
public class CodebaseLogic {

  public Path dir(CodebaseEntity codebase) {
    return GitUtils.extractRootDir(DirectoryManager.CODEBASE_ROOT, codebase.getUrl());
  }

  public Path resolve(CodebaseEntity codebase, String relativePath) {
    return dir(codebase).resolve(relativePath);
  }

  public boolean exists(CodebaseEntity codebase) {
    return dir(codebase).toFile().exists();
  }
}

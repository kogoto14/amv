package dev.aulait.amv.domain.project;

import dev.aulait.amv.arch.util.JsonUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CodebaseConfigFdo {
  private BuildConfig build = new BuildConfig();

  public static CodebaseConfigFdo load(Path codebaseDir) {
    Path configFile = codebaseDir.resolve("amv-config.json");

    if (Files.notExists(configFile)) {
      return new CodebaseConfigFdo();
    }

    return JsonUtils.path2obj(configFile, CodebaseConfigFdo.class);
  }

  @Data
  public static class BuildConfig {
    private String buildFilePattern;
    private List<String> includedProjects = new ArrayList<>();
    private List<String> excludedProjects = new ArrayList<>();
    private boolean compile = true;
    private boolean compileTests = false;
    private boolean failNever = false;
    private String args = "";
  }
}

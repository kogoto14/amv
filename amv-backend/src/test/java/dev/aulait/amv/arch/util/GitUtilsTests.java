package dev.aulait.amv.arch.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aulait.amv.arch.test.FileUtils4Test;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class GitUtilsTests {

  @Test
  void testCloneAndGetRemoteUrl() {
    String repoUrl = "https://github.com/project-au-lait/project-au-lait.github.io.git";

    Path clonedDir = GitUtils.gitClone(Path.of("target"), repoUrl, true);

    FileUtils4Test.deleteOnExit(clonedDir);

    assertTrue(Files.exists(clonedDir));

    String remoteUrl = GitUtils.getRemoteUrl(clonedDir);
    assertEquals(repoUrl, remoteUrl);
  }

  @Test
  void testGitCloneWithToken() {

    String repoUrl = "https://github.com/ykuwahara/amv-test-repository.git";
    Path repoDir =
        GitUtils.gitClone(
            Path.of("target"),
            repoUrl,
            "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",
            "ID_TEST");

    FileUtils4Test.deleteOnExit(repoDir);

    assertTrue(Files.exists(repoDir));
  }
}

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

    String repoUrl = "https://github.com/project-au-lait/amv-test-repository.git";
    Path repoDir =
        GitUtils.gitClone(
            Path.of("target"),
            repoUrl,
            // TODO: Consider how to encrypt and store the token.
            "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",
            "ID_TEST");

    FileUtils4Test.deleteOnExit(repoDir);

    assertTrue(Files.exists(repoDir));
  }
}

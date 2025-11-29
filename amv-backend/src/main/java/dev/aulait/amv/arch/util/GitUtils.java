package dev.aulait.amv.arch.util;

import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.arch.util.ExecUtils.ExecResultVo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class GitUtils {

  private static final String ASK_PASS_SH =
      """
      #!/bin/sh
      case "$1" in
        Username*) echo "${username}" ;;
        Password*) echo "${password}" ;;
      esac
      """;

  /*
  The username is required (an empty string is not allowed), but it is not used for authentication, so an arbitrary string is used.
  https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#using-a-personal-access-token-on-the-command-line
  */
  private static final String USER_NAME = "arbitrary_string";

  static Path createAskPass(String token, String id) {
    String askPassSh =
        StringSubstitutor.replace(ASK_PASS_SH, Map.of("username", USER_NAME, "password", token));

    Path askPassPath = FileUtils.write(Path.of(id + "_askpass.sh"), askPassSh);
    askPassPath.toFile().setExecutable(true);

    return askPassPath;
  }

  public static Path gitClone(Path outDir, String url, String token, String id) {
    return gitCloneWithToken(outDir, url, false, token, id);
  }

  public static Path gitClone(Path outDir, String url) {
    return gitClone(outDir, url, false);
  }

  public static Path gitClone(Path outDir, String url, boolean isMirror) {

    String gitCmd = "git -C ${outDir} clone ${mirror} ${url}";
    String mirror = isMirror ? "--mirror" : "";
    gitCmd = StringSubstitutor.replace(gitCmd, Map.of("mirror", mirror));

    int exitCode = ExecUtils.exec(gitCmd, Map.of("outDir", outDir, "url", url));

    if (exitCode != 0) {
      throw new GitException("git clone failed. url=" + url);
    }

    Path clonedDir = extractRootDir(outDir, url);

    log.info("Git clone completed. url={}, dir={}", url, clonedDir);

    return clonedDir;
  }

  public static Path gitCloneWithToken(
      Path outDir, String url, boolean isMirror, String token, String id) {

    String gitCmd = "git -C ${outDir} clone -c credential.helper= ${mirror} ${url}";
    String mirror = isMirror ? "--mirror" : "";
    gitCmd = StringSubstitutor.replace(gitCmd, Map.of("mirror", mirror));
    Path askPassPath = createAskPass(token, id);

    int exitCode =
        ExecUtils.exec(
            gitCmd,
            Map.of("outDir", outDir, "url", url),
            Map.of("GIT_ASKPASS", askPassPath.toAbsolutePath().toString()));

    FileUtils.delete(askPassPath);

    // Todo: Review error handling after listing the use cases.
    if (exitCode != 0) {
      throw new GitException("git clone failed. url=" + url);
    }

    Path clonedDir = extractRootDir(outDir, url);

    log.info("Git clone completed. url={}, dir={}", url, clonedDir);

    return clonedDir;
  }

  public static String extractRootDirName(String gitUrl) {
    String dir = StringUtils.substringAfterLast(gitUrl, "/");
    dir = dir.replace(".git", "");
    return dir;
  }

  public static Path extractRootDir(Path outDir, String gitUrl) {
    String dir = StringUtils.substringAfterLast(gitUrl, "/");

    Path rootDir = outDir.resolve(dir).toAbsolutePath().normalize();

    if (Files.exists(rootDir)) {
      return rootDir;
    }

    dir = dir.replace(".git", "");
    return outDir.resolve(dir).toAbsolutePath().normalize();
  }

  public static String getRemoteUrl(Path repoDir) {
    String gitCmd = "git -C ${repoDir} remote get-url origin";

    ExecResultVo result = ExecUtils.execWithResult(gitCmd, Map.of("repoDir", repoDir));

    return result.getOut();
  }

  public static String getCurrentHash(Path repoDir) {
    String gitCmd = "git -C ${repoDir} rev-parse HEAD";

    ExecResultVo result = ExecUtils.execWithResult(gitCmd, Map.of("repoDir", repoDir));

    return result.getOut();
  }

  public static String getCurrentBranch(Path repoDir) {
    String gitCmd = "git -C ${repoDir} rev-parse --abbrev-ref HEAD";

    ExecResultVo result = ExecUtils.execWithResult(gitCmd, Map.of("repoDir", repoDir));

    return result.getOut();
  }

  public static class GitException extends RuntimeException {
    public GitException(String message) {
      super(message);
    }
  }
}

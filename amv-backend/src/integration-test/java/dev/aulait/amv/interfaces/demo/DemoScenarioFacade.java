package dev.aulait.amv.interfaces.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.arch.async.AsyncExecStatus;
import dev.aulait.amv.arch.async.AsyncExecWsClient;
import dev.aulait.amv.arch.file.DirectoryManager;
import dev.aulait.amv.arch.test.TestConfig;
import dev.aulait.amv.arch.util.GitUtils;
import dev.aulait.amv.interfaces.project.CodebaseClient;
import dev.aulait.amv.interfaces.project.CodebaseController.CodebaseSearchResultDto;
import dev.aulait.amv.interfaces.project.CodebaseDto;
import dev.aulait.amv.interfaces.project.CodebaseSearchCriteriaDto;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class DemoScenarioFacade {

  private static final String DEMO_CODEBASE_NAME = "AMV";
  private static final String DEMO_CODEBASE_URL =
      "https://github.com/project-au-lait/amv.git";

  private AsyncExecWsClient asyncExecWsClient = new AsyncExecWsClient();
  private CodebaseClient codebaseClient = new CodebaseClient();

  private static final DemoScenarioFacade SINGLETON = new DemoScenarioFacade();

  public static DemoScenarioFacade getInstance() {
    return SINGLETON;
  }

  public synchronized void runIfNotLoaded() {
    if (isLoaded()) {
      log.info("Demo codebase is already loaded.");
    } else {
      log.info("Loading demo codebase...");
      run();
    }
  }

  public boolean isLoaded() {
    CodebaseSearchCriteriaDto request = new CodebaseSearchCriteriaDto();
    request.setText(DEMO_CODEBASE_NAME);
    CodebaseSearchResultDto result = codebaseClient.search(request);
    return !result.getList().isEmpty();
  }

  public void run() {
    Path mirrorRepo = DirectoryManager.MOUNT_DIR_HOST.resolve("amv.git");

    if (!Files.exists(mirrorRepo)) {
      GitUtils.gitClone(DirectoryManager.MOUNT_DIR_HOST, DEMO_CODEBASE_URL, true);
    }

    Path mountDir =
        TestConfig.getInstance().isUsingContainer()
            ? DirectoryManager.MOUNT_DIR_CONTAINER
            : DirectoryManager.MOUNT_DIR_HOST;
    String codebaseUrl = mountDir.resolve("amv.git").toString();
    codebaseUrl = codebaseUrl.replace("C:", "").replace("\\", "/");

    CodebaseDto codebaseDto =
        CodebaseDto.builder().name(DEMO_CODEBASE_NAME).url(codebaseUrl).build();

    String codebaseId = codebaseClient.save(codebaseDto);

    codebaseClient.analyze(codebaseId);

    assertEquals(AsyncExecStatus.COMPLETED, asyncExecWsClient.waitUntilFinished(codebaseId));
  }
}

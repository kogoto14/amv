package dev.aulait.amv.interfaces.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aulait.amv.arch.async.AsyncExecStatus;
import dev.aulait.amv.arch.async.AsyncExecWsClient;
import dev.aulait.amv.arch.exception.ErrorResponseDto;
import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.arch.util.ExecUtils;
import dev.aulait.amv.interfaces.project.CodebaseController.CodebaseSearchResultDto;
import jakarta.ws.rs.core.Response.Status;
import java.nio.file.Path;
import java.util.Map;
import org.codehaus.plexus.util.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * This integration test is automatically generated.
 *
 * <p>The test generated is a sample that only checks the id. Tests will fail depending on the
 * entity configuration.
 *
 * <p>Change the content of the test if necessary.
 */
class CodebaseControllerIT {

  CodebaseClient codebaseClient = new CodebaseClient();

  AsyncExecWsClient asyncExecWsClient = new AsyncExecWsClient();

  @Test
  void testCrud() {
    CodebaseDto dto = CodebaseDataFactory.createCodebase();
    String id = dto.getId();

    // Create
    String createdId = codebaseClient.save(dto);
    assertEquals(id, createdId);

    // Reference
    CodebaseDto refDto = codebaseClient.get(id);
    assertEquals(id, refDto.getId());

    // Update
    // TODO Implementation of assembling a request and assertion
    String updatedId = codebaseClient.update(id, refDto);

    // Search
    CodebaseSearchCriteriaDto criteria = new CodebaseSearchCriteriaDto();
    criteria.setText(dto.getName());
    CodebaseSearchResultDto result = codebaseClient.search(criteria);
    assertTrue(result.getList().size() >= 1);

    CodebaseDto updatedCodebase = codebaseClient.get(id);

    // Delete
    String deletedId = codebaseClient.delete(id, updatedCodebase);
    assertEquals(deletedId, id);

    ErrorResponseDto error = codebaseClient.getWithError(id);
    assertEquals(Status.NOT_FOUND, error.getStatus());
  }

  @Test
  void testWithToken() {
    String token =
        "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";
    String repoName = "amv-test-repository";
    CodebaseDto dto =
        CodebaseDto.builder()
            .name(repoName)
            .url("https://github.com/project-au-lait/amv-test-repository.git")
            .token(token)
            .build();

    String createdId = codebaseClient.save(dto);

    CodebaseDto refDto = codebaseClient.get(createdId);
    // The host does not have the key on the container, so it cannot be decrypted.
    assertNotEquals(token, refDto.getToken());
    assertTrue(refDto.getToken().startsWith("amvenc:"));

    codebaseClient.analyze(createdId);

    assertEquals(AsyncExecStatus.COMPLETED, asyncExecWsClient.waitUntilFinished(createdId));

    Path workingDirectory = Path.of("target");

    String dockerPsCmd = "docker ps -q -f \"name=amv-container-back\"";
    String containerId = ExecUtils.execWithResult(dockerPsCmd, Map.of(), workingDirectory).getOut();

    String dockerCpCmd =
        String.format(
            "docker cp %s:/deployments/amv/codebase/amv-test-repository/README.md README.md",
            containerId);

    Path tgtFilePath = Path.of("target/README.md");

    // Copy the files cloned inside the container
    ExecUtils.execWithResult(dockerCpCmd, Map.of(), workingDirectory);
    String fileString = FileUtils.read(tgtFilePath);

    // Check if the contents of the file are correct
    assertTrue(StringUtils.contains(fileString, repoName));

    FileUtils.delete(tgtFilePath);
  }
}

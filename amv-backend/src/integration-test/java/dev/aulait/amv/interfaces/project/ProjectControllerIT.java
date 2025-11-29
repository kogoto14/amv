package dev.aulait.amv.interfaces.project;

import static dev.aulait.amv.interfaces.project.ProjectController.ProjectSearchResultDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aulait.amv.arch.exception.ErrorResponseDto;
import jakarta.ws.rs.core.Response.Status;

/**
 * This integration test is automatically generated.
 *
 * <p>The test generated is a sample that only checks the id. Tests will fail depending on the
 * entity configuration.
 *
 * <p>Change the content of the test if necessary.
 */
class ProjectControllerIT {

  ProjectClient client = new ProjectClient();

  // TODO Enable test
  // @Test
  void testCrud() {
    ProjectDto dto = ProjectDataFactory.createProject();
    String id = dto.getId();

    // Create
    String createdId = client.save(dto);
    assertEquals(id, createdId);

    // Reference
    ProjectDto refDto = client.get(id);
    assertEquals(id, refDto.getId());

    // Update
    // TODO Implementation of assembling a request and assertion
    String updatedId = client.update(id, refDto);

    // Search
    ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto();
    ProjectSearchResultDto result = client.search(criteria);
    assertTrue(result.getList().size() > 1);

    ProjectDto updatedProject = client.get(id);

    // Delete
    String deletedId = client.delete(id, updatedProject);
    assertEquals(deletedId, id);

    ErrorResponseDto error = client.getWithError(id);
    assertEquals(Status.NOT_FOUND, error.getStatus());
  }
}

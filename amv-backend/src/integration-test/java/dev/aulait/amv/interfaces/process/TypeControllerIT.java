package dev.aulait.amv.interfaces.process;

import static dev.aulait.amv.interfaces.process.TypeController.TypeSearchResultDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.aulait.amv.arch.exception.ErrorResponseDto;
import dev.aulait.amv.interfaces.process.TypeController.TypeSearchResultDto;
import jakarta.ws.rs.core.Response.Status;

/**
 * This integration test is automatically generated.
 *
 * <p>The test generated is a sample that only checks the id. Tests will fail depending on the
 * entity configuration.
 *
 * <p>Change the content of the test if necessary.
 */
class TypeControllerIT {

  TypeClient client = new TypeClient();

  // TODO Enable test
  // @Test
  void testCrud() {
    TypeDto dto = TypeDataFactory.createType();
    String id = dto.getId();

    // Create
    String createdId = client.save(dto);
    assertEquals(id, createdId);

    // Reference
    TypeDto refDto = client.get(id);
    assertEquals(id, refDto.getId());

    // Update
    // TODO Implementation of assembling a request and assertion
    String updatedId = client.update(id, refDto);

    // Search
    TypeSearchCriteriaDto criteria = new TypeSearchCriteriaDto();
    TypeSearchResultDto result = client.search(criteria);
    assertTrue(result.getList().size() > 1);

    TypeDto updatedType = client.get(id);

    // Delete
    String deletedId = client.delete(id, updatedType);
    assertEquals(deletedId, id);

    ErrorResponseDto error = client.getWithError(id);
    assertEquals(Status.NOT_FOUND, error.getStatus());
  }
}

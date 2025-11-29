package dev.aulait.amv.arch.async;

import dev.aulait.amv.arch.exception.ResourceNotFoundException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@Path(AsyncExecController.BASE_PATH)
@RequiredArgsConstructor
public class AsyncExecController {

  static final String BASE_PATH = "async";

  static final String STATUS_PATH = "{execId}";

  private final AsyncExecService asyncService;

  @Path(STATUS_PATH)
  @GET
  public String getStatus(String execId) {
    AsyncExecStatus status = asyncService.getStatus(execId);
    if (status == null) {
      throw new ResourceNotFoundException(execId);
    }
    return status.name();
  }
}

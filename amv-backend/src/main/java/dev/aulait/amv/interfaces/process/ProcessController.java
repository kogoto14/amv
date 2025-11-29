package dev.aulait.amv.interfaces.process;

import dev.aulait.amv.domain.process.LoadStatusDto;
import dev.aulait.amv.domain.process.LoadStatusService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@Path(ProcessController.BASE_PATH)
@RequiredArgsConstructor
public class ProcessController {

  static final String BASE_PATH = "processes";

  static final String LOAD_PATH = "/load";

  static final String STATUS_PATH = "/status";

  private final LoadStatusService loadStatusService;

  @GET
  @Path(LOAD_PATH + STATUS_PATH)
  public LoadStatusDto get() {
    return new LoadStatusDto(loadStatusService.getStatus());
  }
}

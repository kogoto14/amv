package dev.aulait.amv.interfaces.process;

import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodService;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchResult;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@Path(MethodController.BASE_PATH)
@RequiredArgsConstructor
public class MethodController {

  static final String BASE_PATH = "/methods";
  static final String METHOD_SEARCH_PATH = "search";
  private final MethodService methodService;
  private final MethodFactory methodFactory;

  public static class MethodSearchResultDto extends SearchResult<MethodDto> {}

  @POST
  @Path(METHOD_SEARCH_PATH)
  public MethodSearchResultDto search(MethodSearchCriteriaDto dto) {
    SearchCriteria searchCriteria = methodFactory.build(dto);
    SearchResult<MethodEntity> result = methodService.search(searchCriteria);

    return methodFactory.build(result);
  }
}

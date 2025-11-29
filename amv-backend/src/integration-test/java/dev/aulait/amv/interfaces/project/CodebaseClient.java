package dev.aulait.amv.interfaces.project;

import static dev.aulait.amv.interfaces.project.CodebaseController.*;

import dev.aulait.amv.arch.exception.ErrorResponseDto;
import dev.aulait.amv.arch.test.RestClientUtils;
import dev.aulait.amv.interfaces.project.CodebaseController.CodebaseSearchResultDto;

public class CodebaseClient {

  private static final String CODEBASE_AND_CODEBASE_ID_PATH =
      CODEBASE_PATH + "/" + CODEBASE_ID_PATH;

  public CodebaseDto get(String id) {
    return RestClientUtils.get(CODEBASE_AND_CODEBASE_ID_PATH, CodebaseDto.class, id.toString());
  }

  public ErrorResponseDto getWithError(String id) {
    return RestClientUtils.getWithError(CODEBASE_AND_CODEBASE_ID_PATH, id.toString());
  }

  public String save(CodebaseDto dto) {
    return RestClientUtils.post(CODEBASE_PATH, dto, String.class);
  }

  public String update(String id, CodebaseDto dto) {
    return RestClientUtils.put(CODEBASE_AND_CODEBASE_ID_PATH, dto, String.class, id);
  }

  public String delete(String id, CodebaseDto dto) {
    return RestClientUtils.delete(CODEBASE_AND_CODEBASE_ID_PATH, dto, String.class, id);
  }

  public CodebaseSearchResultDto search(CodebaseSearchCriteriaDto dto) {
    return RestClientUtils.post(
        CODEBASE_PATH + "/" + CODEBASE_SEARCH_PATH, dto, CodebaseSearchResultDto.class);
  }

  public String load(String id) {
    return RestClientUtils.post(CODEBASE_PATH + "/" + LOAD_PATH, "", String.class, id);
  }

  public String analyze(String id) {
    return RestClientUtils.post(CODEBASE_PATH + "/" + ANALYZE_PATH, "", String.class, id);
  }
}

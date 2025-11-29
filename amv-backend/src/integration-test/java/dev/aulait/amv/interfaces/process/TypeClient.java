package dev.aulait.amv.interfaces.process;

import static dev.aulait.amv.interfaces.process.TypeController.*;
import static dev.aulait.amv.interfaces.process.TypeController.TypeSearchResultDto;
import dev.aulait.amv.arch.exception.ErrorResponseDto;
import dev.aulait.amv.arch.test.RestClientUtils;

public class TypeClient {

  private static final String TYPE_AND_TYPE_ID_PATH = TYPE_PATH + "/" + TYPE_ID_PATH;

  public TypeDto get(String id) {
    return RestClientUtils.get(TYPE_AND_TYPE_ID_PATH, TypeDto.class, id.toString());
  }

  public ErrorResponseDto getWithError(String id) {
    return RestClientUtils.getWithError(TYPE_AND_TYPE_ID_PATH, id.toString());
  }

  public String save(TypeDto dto) {
    return RestClientUtils.post(TYPE_PATH, dto, String.class);
  }

  public String update(String id, TypeDto dto) {
    return RestClientUtils.put(TYPE_AND_TYPE_ID_PATH, dto, String.class, id.toString());
  }

  public String delete(String id, TypeDto dto) {
    return RestClientUtils.delete(TYPE_AND_TYPE_ID_PATH, dto, String.class, id.toString());
  }

  public TypeSearchResultDto search(TypeSearchCriteriaDto dto) {
    return RestClientUtils.post(TYPE_PATH + "/" + TYPE_SEARCH_PATH, dto, TypeSearchResultDto.class);
  }
}
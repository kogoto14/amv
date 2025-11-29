package dev.aulait.amv.interfaces.project;

import static dev.aulait.amv.interfaces.project.ProjectController.*;
import static dev.aulait.amv.interfaces.project.ProjectController.ProjectSearchResultDto;
import dev.aulait.amv.arch.exception.ErrorResponseDto;
import dev.aulait.amv.arch.test.RestClientUtils;

public class ProjectClient {

  private static final String PROJECT_AND_PROJECT_ID_PATH = PROJECT_PATH + "/" + PROJECT_ID_PATH;

  public ProjectDto get(String id) {
    return RestClientUtils.get(PROJECT_AND_PROJECT_ID_PATH, ProjectDto.class, id.toString());
  }

  public ErrorResponseDto getWithError(String id) {
    return RestClientUtils.getWithError(PROJECT_AND_PROJECT_ID_PATH, id.toString());
  }

  public String save(ProjectDto dto) {
    return RestClientUtils.post(PROJECT_PATH, dto, String.class);
  }

  public String update(String id, ProjectDto dto) {
    return RestClientUtils.put(PROJECT_AND_PROJECT_ID_PATH, dto, String.class, id.toString());
  }

  public String delete(String id, ProjectDto dto) {
    return RestClientUtils.delete(PROJECT_AND_PROJECT_ID_PATH, dto, String.class, id.toString());
  }

  public ProjectSearchResultDto search(ProjectSearchCriteriaDto dto) {
    return RestClientUtils.post(PROJECT_PATH + "/" + PROJECT_SEARCH_PATH, dto, ProjectSearchResultDto.class);
  }
}
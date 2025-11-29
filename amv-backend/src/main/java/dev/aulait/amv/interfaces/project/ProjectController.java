package dev.aulait.amv.interfaces.project;

import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.domain.project.ProjectEntity;
import dev.aulait.amv.domain.project.ProjectService;
import dev.aulait.sqb.SearchCriteria;
import dev.aulait.sqb.SearchResult;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;

@Path(ProjectController.PROJECT_PATH)
@RequiredArgsConstructor
public class ProjectController {

  private final ProjectService projectService;
  private final ProjectFactory projectFactory;

  static final String PROJECT_PATH = "project";
  static final String PROJECT_ID_PATH = "{id}";
  static final String PROJECT_SEARCH_PATH = "search";

  public static class ProjectSearchResultDto extends SearchResult<ProjectDto> {}

  @GET
  @Path(PROJECT_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public ProjectDto get(@PathParam("id") String id) {
    ProjectEntity entity = projectService.find(id);

    return BeanUtils.map(entity, ProjectDto.class);
  }

  @POST
  public String save(@Valid ProjectDto dto) {
    ProjectEntity entity = BeanUtils.map(dto, ProjectEntity.class);

    ProjectEntity savedEntity = projectService.save(entity);

    return savedEntity.getId();
  }

  @PUT
  @Path(PROJECT_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public String update(@PathParam("id") String id, @Valid ProjectDto dto) {
    ProjectEntity entity = BeanUtils.map(dto, ProjectEntity.class);

    entity.setId(id);

    ProjectEntity updatedEntity = projectService.save(entity);

    return updatedEntity.getId();
  }

  @DELETE
  @Path(PROJECT_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public String delete(@PathParam("id") String id, @Valid ProjectDto dto) {
    ProjectEntity entity = BeanUtils.map(dto, ProjectEntity.class);

    entity.setId(id);

    projectService.delete(entity);

    return entity.getId();
  }

  @POST
  @Path(PROJECT_SEARCH_PATH)
  public ProjectSearchResultDto search(ProjectSearchCriteriaDto dto) {
    SearchCriteria searchCriteria = projectFactory.build(dto);
    SearchResult<ProjectEntity> result = projectService.search(searchCriteria);

    return projectFactory.build(result);
  }
}

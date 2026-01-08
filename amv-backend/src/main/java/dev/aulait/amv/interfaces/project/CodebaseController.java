package dev.aulait.amv.interfaces.project;

import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.domain.project.CodebaseAggregate;
import dev.aulait.amv.domain.project.CodebaseEntity;
import dev.aulait.amv.domain.project.CodebaseFacade;
import dev.aulait.amv.domain.project.CodebaseService;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;

@Path(CodebaseController.CODEBASE_PATH)
@RequiredArgsConstructor
public class CodebaseController {

  private final CodebaseService codebaseService;
  private final CodebaseFactory codebaseFactory;
  private final ProjectService projectService;
  private final CodebaseFacade codebaseFacade;

  static final String CODEBASE_PATH = "codebases";
  static final String CODEBASE_ID_PATH = "{id}";
  static final String CODEBASE_NAME_PATH = "by-name/{name}";
  static final String CODEBASE_SEARCH_PATH = "search";
  static final String LOAD_PATH = "load/{id}";
  static final String ALL_PATH = "all";
  static final String ANALYZE_PATH = "analyze/{id}";

  public static class CodebaseSearchResultDto extends SearchResult<CodebaseDto> {}

  @GET
  @Path(CODEBASE_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public CodebaseDto get(@PathParam("id") String id) {
    CodebaseAggregate codebase = codebaseService.findWithProjects(id);

    return codebaseFactory.build(codebase);
  }

  @GET
  @Path(CODEBASE_NAME_PATH)
  @Parameters({@Parameter(name = "name", in = ParameterIn.PATH, required = true)})
  public CodebaseDto getByName(@PathParam("name") String name) {
    return codebaseService.findByNameWithProjects(name).map(codebaseFactory::build).orElse(null);
  }

  @POST
  public String save(@Valid CodebaseDto dto) {
    CodebaseEntity entity = BeanUtils.map(dto, CodebaseEntity.class);

    CodebaseEntity savedEntity = codebaseService.save(entity);

    return savedEntity.getId();
  }

  @PUT
  @Path(CODEBASE_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public String update(@PathParam("id") String id, @Valid CodebaseDto dto) {
    CodebaseEntity entity = BeanUtils.map(dto, CodebaseEntity.class);

    entity.setId(id);

    CodebaseEntity updatedEntity = codebaseService.save(entity);

    return updatedEntity.getId();
  }

  @DELETE
  @Path(CODEBASE_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public String delete(@PathParam("id") String id, @Valid CodebaseDto dto) {
    CodebaseEntity entity = BeanUtils.map(dto, CodebaseEntity.class);

    entity.setId(id);

    codebaseService.delete(entity);

    return entity.getId();
  }

  @POST
  @Path(CODEBASE_SEARCH_PATH)
  public CodebaseSearchResultDto search(CodebaseSearchCriteriaDto dto) {
    SearchCriteria searchCriteria = codebaseFactory.build(dto);
    SearchResult<CodebaseEntity> result = codebaseService.search(searchCriteria);

    return codebaseFactory.build(result);
  }

  @POST
  @Path(LOAD_PATH)
  public String load(@PathParam("id") String id) {
    return codebaseService.loadAllAsync(id);
  }

  @GET
  @Path(ALL_PATH)
  public List<CodebaseDto> findAll() {
    return codebaseFactory.build(codebaseService.findAllWithProjects());
  }

  @POST
  @Path(ANALYZE_PATH)
  public void analyze(@PathParam("id") String id) {
    codebaseFacade.analyzeAsync(id);
  }
}

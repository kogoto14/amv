package dev.aulait.amv.interfaces.process;

import dev.aulait.amv.arch.util.BeanUtils;
import dev.aulait.amv.domain.process.TypeEntity;
import dev.aulait.amv.domain.process.TypeService;
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

@Path(TypeController.TYPE_PATH)
@RequiredArgsConstructor
public class TypeController {

  private final TypeService typeService;
  private final TypeFactory typeFactory;

  static final String TYPE_PATH = "type";
  static final String TYPE_ID_PATH = "{idOrQualifiedName}";
  static final String TYPE_SEARCH_PATH = "search";

  public static class TypeSearchResultDto extends SearchResult<TypeDto> {}

  @GET
  @Path(TYPE_ID_PATH)
  public TypeDto get(@PathParam("idOrQualifiedName") String idOrQualifiedName) {
    TypeEntity entity = typeService.find(idOrQualifiedName);

    return typeFactory.build(entity);
  }

  @POST
  public String save(@Valid TypeDto dto) {
    TypeEntity entity = BeanUtils.map(dto, TypeEntity.class);

    TypeEntity savedEntity = typeService.save(entity);

    return savedEntity.getId();
  }

  @PUT
  @Path(TYPE_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public String update(@PathParam("id") String id, @Valid TypeDto dto) {
    TypeEntity entity = BeanUtils.map(dto, TypeEntity.class);

    entity.setId(id);

    TypeEntity updatedEntity = typeService.save(entity);

    return updatedEntity.getId();
  }

  @DELETE
  @Path(TYPE_ID_PATH)
  @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, required = true)})
  public String delete(@PathParam("id") String id, @Valid TypeDto dto) {
    TypeEntity entity = BeanUtils.map(dto, TypeEntity.class);

    entity.setId(id);

    typeService.delete(entity);

    return entity.getId();
  }

  @POST
  @Path(TYPE_SEARCH_PATH)
  public TypeSearchResultDto search(TypeSearchCriteriaDto dto) {
    SearchCriteria searchCriteria = typeFactory.build(dto);
    SearchResult<TypeEntity> result = typeService.search(searchCriteria);

    return typeFactory.build(result);
  }
}

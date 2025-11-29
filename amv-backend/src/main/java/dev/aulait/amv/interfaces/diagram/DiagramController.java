package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.domain.diagram.CallTreeService;
import dev.aulait.amv.domain.diagram.CallTreeVo;
import dev.aulait.amv.domain.diagram.CrudElementVo;
import dev.aulait.amv.domain.diagram.DiagramService;
import dev.aulait.amv.domain.process.MethodService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Path(DiagramController.BASE_PATH)
@RequiredArgsConstructor
public class DiagramController {

  static final String BASE_PATH = "/diagrams";

  static final String CALL_TREE_PATH = "/call-tree";

  static final String CRUD_PATH = "/crud";

  private final DiagramService service;

  private final CallTreeService callTreeService;

  private final MethodService methodService;

  private final CallTreeFactory callTreeFactory;

  private final CrudFactory crudFactory;

  @POST
  @Path(CALL_TREE_PATH)
  public List<CallTreeDto> callTree(CallTreeCriteriaDto criteria) {
    List<CallTreeVo> callTrees =
        callTreeService.buildCallTree(
            criteria.getSignaturePattern(),
            criteria.isCallTreeRequired(),
            criteria.isCalledTreeRequired(),
            criteria.getLimit());

    Set<String> typeIds =
        callTrees.stream().flatMap(ct -> ct.collectTypeIds().stream()).collect(Collectors.toSet());
    Map<String, String> typeId2url = methodService.resolveUrl(typeIds);

    return callTreeFactory.build(callTrees, typeId2url);
  }

  @GET
  @Path(CRUD_PATH)
  public CrudDto getCrudDiagram() {
    List<CrudElementVo> elements = service.getCrudDiagram();

    Set<String> typeIds =
        elements.stream()
            .map(CrudElementVo::getMethod)
            .map(method -> method.getId().getTypeId())
            .collect(Collectors.toSet());
    Map<String, String> typeId2url = methodService.resolveUrl(typeIds);

    return crudFactory.build(elements, typeId2url);
  }

  @GET
  @Path("/class")
  public String classDiagram(@QueryParam("qualifiedName") String qualifiedName) {
    return service.generateClassDiagram(List.of(qualifiedName)).getImage();
  }
}

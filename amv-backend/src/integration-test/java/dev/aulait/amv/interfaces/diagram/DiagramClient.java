package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.arch.test.RestClientUtils;
import io.restassured.common.mapper.TypeRef;
import java.util.List;

public class DiagramClient {

  public List<CallTreeDto> callTree(String signaturePattern) {
    CallTreeCriteriaDto request = new CallTreeCriteriaDto();
    request.setSignaturePattern(signaturePattern);
    return RestClientUtils.postAsList(
        DiagramController.BASE_PATH + DiagramController.CALL_TREE_PATH,
        request,
        new TypeRef<List<CallTreeDto>>() {});
  }
}

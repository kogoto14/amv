package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.MethodCallProjection;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodEntityId;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CallTreeLogic {

  @Setter private Function<MethodEntityId, List<MethodCallProjection>> findMethodCallByCalleeId;

  private static final int MAX_DEPTH = 10;

  public List<CallTreeElementVo> buildCallTree(MethodEntity method) {
    List<CallTreeElementVo> elements = new ArrayList<>();

    addCallTree(method, 0, elements, "", 0);

    return elements;
  }

  private void addCallTree(
      MethodEntity method,
      int depth,
      List<CallTreeElementVo> elements,
      String calledTypeId,
      int calledLineNo) {

    if (depth > MAX_DEPTH) {
      log.debug("max depth exceeded: {}:{}", method.getId(), method.getQualifiedSignature());
      return;
    }

    elements.add(CallTreeElementVo.callTree(depth, method, calledTypeId, calledLineNo));

    method.getMethodCalls().stream()
        .sorted(Comparator.comparing(methodCall -> methodCall.getLineNo()))
        .forEach(
            methodCall -> {
              if (methodCall.getMethod() == null) {
                elements.add(
                    CallTreeElementVo.callTree(depth + 1, methodCall, method.getId().getTypeId()));
              } else {
                addCallTree(
                    methodCall.getMethod(),
                    depth + 1,
                    elements,
                    method.getId().getTypeId(),
                    methodCall.getLineNo());
              }
            });
  }

  public List<CallTreeElementVo> buildCalledTree(MethodEntity method) {
    List<CallTreeElementVo> elements = new ArrayList<>();

    addCalledTree(method, 0, elements, 0);

    return elements;
  }

  private void addCalledTree(
      MethodEntity method, int depth, List<CallTreeElementVo> elements, int calledLineNo) {

    if (depth > MAX_DEPTH) {
      log.debug("max depth exceeded: {}:{}", method.getId(), method.getQualifiedSignature());
      return;
    }

    elements.add(CallTreeElementVo.calledTree(depth, method, calledLineNo));

    List<MethodCallProjection> callees = findMethodCallByCalleeId.apply(method.getId());
    for (MethodCallProjection callee : callees) {
      addCalledTree(callee.getMethod(), depth + 1, elements, callee.getMethodCall().getLineNo());
    }
  }
}

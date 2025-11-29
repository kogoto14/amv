package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.CrudPointEntity;
import dev.aulait.amv.domain.process.MethodCallEntity;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.TypeEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
@Slf4j
public class CrudDiagramLogic {

  private static final int MAX_DEPTH = 10;

  private final Function<String, Optional<TypeEntity>> typeResolver;

  public List<CrudElementVo> getCrudDiagram(List<MethodEntity> entryPointMethods) {
    List<CrudElementVo> diagram = new ArrayList<>();

    for (MethodEntity method : entryPointMethods) {
      List<CrudPointEntity> crudPoints = new ArrayList<>();
      collectCrud(null, method, crudPoints, 0);

      for (CrudPointEntity crudPoint : crudPoints) {
        CrudElementVo element =
            CrudElementVo.builder()
                .entryPoint(method.getEntryPoint().getPath())
                .method(method)
                .table(Objects.toString(crudPoint.getDataName(), crudPoint.getType()))
                .crud(crudPoint.getCrud())
                .build();
        diagram.add(element);
      }
    }

    return diagram;
  }

  void collectCrud(
      MethodCallEntity methodCall,
      MethodEntity calledMethod,
      List<CrudPointEntity> crudPoints,
      int depth) {
    if (log.isTraceEnabled()) {
      log.trace("collectCrud: method={}", calledMethod.getQualifiedSignature());
    }

    if (depth > MAX_DEPTH) {
      log.debug(
          "Max depth exceeded: {}:{}", calledMethod.getId(), calledMethod.getQualifiedSignature());
      return;
    }

    if (calledMethod.getCrudPoints() != null) {
      calledMethod.getCrudPoints().stream()
          .map(crudPoint -> adjust(methodCall, crudPoint))
          .forEach(crudPoints::add);
    }

    calledMethod.getMethodCalls().stream()
        .filter(nextCall -> Objects.nonNull(nextCall.getMethod()))
        .forEach(nextCall -> collectCrud(nextCall, nextCall.getMethod(), crudPoints, depth + 1));
  }

  CrudPointEntity adjust(MethodCallEntity methodCall, CrudPointEntity crudPoint) {
    if (methodCall == null) {
      return crudPoint;
    }

    if (StringUtils.isEmpty(crudPoint.getKind())) {
      return crudPoint;
    }

    CrudPointEntity adjusted = new CrudPointEntity();
    adjusted.setCrud(crudPoint.getCrud());
    typeResolver
        .apply(methodCall.getArgumentTypes())
        .ifPresent(
            type -> {
              adjusted.setType(type.getDataName());
            });

    return adjusted;
  }
}

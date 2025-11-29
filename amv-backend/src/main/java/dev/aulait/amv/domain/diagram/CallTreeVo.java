package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.MethodEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class CallTreeVo {
  private final MethodEntity method;
  private List<CallTreeElementVo> callTrees = new ArrayList<>();
  private List<CallTreeElementVo> calledTrees = new ArrayList<>();

  public Set<String> collectTypeIds() {
    Set<String> typeIds = new HashSet<>();
    typeIds.addAll(collectTypeIds(callTrees));
    typeIds.addAll(collectTypeIds(calledTrees));
    return typeIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
  }

  private Set<String> collectTypeIds(List<CallTreeElementVo> elements) {
    Set<String> typeIds = new HashSet<>();

    elements.stream()
        .filter(e -> e.getMethod().getId() != null)
        .forEach(
            e -> {
              // typeIds.add(e.getMethod().getTypeId());
              typeIds.add(e.getMethod().getId().getTypeId());
              typeIds.add(e.getTypeIdAt());
            });

    return typeIds;
  }
}

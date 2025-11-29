package dev.aulait.amv.interfaces.diagram;

import dev.aulait.amv.domain.diagram.CallTreeElementVo;
import dev.aulait.amv.domain.diagram.CallTreeVo;
import dev.aulait.amv.interfaces.process.MethodDto;
import dev.aulait.amv.interfaces.process.MethodFactory;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
@RequiredArgsConstructor
public class CallTreeFactory {

  private final MethodFactory methodFactory;

  public List<CallTreeDto> build(List<CallTreeVo> vos, Map<String, String> typeId2url) {
    return vos.stream().map(vo -> build(vo, typeId2url)).toList();
  }

  CallTreeDto build(CallTreeVo ct, Map<String, String> typeId2url) {
    MethodDto method = methodFactory.build(ct.getMethod());

    String baseNamespace = method.getNamespace();

    return CallTreeDto.builder()
        .method(method)
        .callTree(ct.getCallTrees().stream().map(e -> build(e, typeId2url, baseNamespace)).toList())
        .calledTree(
            ct.getCalledTrees().stream().map(e -> build(e, typeId2url, baseNamespace)).toList())
        .build();
  }

  CallTreeElementDto build(
      CallTreeElementVo vo, Map<String, String> typeId2url, String baseNamespace) {

    MethodDto method = methodFactory.build(vo.getMethod());

    String declarationUrl =
        method.getId() == null ? "" : typeId2url.get(method.getId().getTypeId());
    method.setSrcUrl(declarationUrl);

    String urlAt = typeId2url.get(vo.getTypeIdAt());
    if (StringUtils.isNotEmpty(urlAt)) {
      urlAt = urlAt + "#L" + vo.getLineNoAt();
    }

    CallTreeElementDto element =
        CallTreeElementDto.builder()
            .call(vo.isCall())
            .method(method)
            .depth(vo.getDepth())
            .lineNoAt(vo.getLineNoAt())
            .urlAt(urlAt)
            .build();

    long sameLevel = countSamePackageLevel(baseNamespace, method.getNamespace());
    element.getElementTags().add("same-package-" + sameLevel);

    if (StringUtils.endsWithAny(
        method.getType(),
        "Dto",
        "Entity",
        "Fdo",
        "Vo",
        "DtoBuilder",
        "EntityBuilder",
        "FdoBuilder",
        "VoBuilder")) {
      element.getTypeTags().add("data");
      if (StringUtils.startsWithAny(method.getName(), "get", "is")) {
        element.getMethodTags().add("getter");
      }

      if (StringUtils.startsWith(method.getName(), "set")) {
        element.getMethodTags().add("setter");
      }
    }

    if (StringUtils.endsWithAny(
        method.getType(), "DtoBuilder", "EntityBuilder", "FdoBuilder", "VoBuilder")) {
      element.getTypeTags().add("data");

      if (!StringUtils.equals(method.getName(), "build")) {
        element.getMethodTags().add("setter");
      }
    }

    return element;
  }

  private long countSamePackageLevel(String base, String target) {
    if (StringUtils.isAnyEmpty(base, target)) return 0;

    String[] baseParts = base.split("\\.");
    String[] targetParts = target.split("\\.");
    int minLength = Math.min(baseParts.length, targetParts.length);

    return IntStream.range(0, minLength)
        .takeWhile(i -> baseParts[i].equals(targetParts[i]))
        .count();
  }
}

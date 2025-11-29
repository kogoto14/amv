package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.arch.util.SyntaxUtils;
import dev.aulait.amv.domain.process.FieldEntity;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.TypeEntity;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class ClassDiagramLogic {

  private static final int MAX_RECURSION_DEPTH = 10;

  private final Function<String, Optional<TypeEntity>> typeFinder;

  public DiagramVo generateClassDiagram(List<TypeEntity> types) {
    StringBuilder sb = new StringBuilder();
    sb.append("@startuml\n");
    sb.append("skinparam linetype ortho\n");

    Set<String> writtenTypes = new HashSet<>();

    for (TypeEntity type : types) {
      sb.append(write(type, 0, writtenTypes));
    }

    sb.append("@enduml");
    return new DiagramVo(sb.toString());
  }

  String write(TypeEntity type, int depth, Set<String> writtenTypes) {
    if (depth > MAX_RECURSION_DEPTH || writtenTypes.contains(type.getQualifiedName())) {
      return "";
    }

    writtenTypes.add(type.getQualifiedName());

    StringBuilder sb = new StringBuilder();

    sb.append(write(type));

    for (var field : type.getFields()) {
      String fieldTypeName = field.getType();

      if (StringUtils.contains(fieldTypeName, "<")) {
        fieldTypeName = StringUtils.substringBetween(fieldTypeName, "<", ">");
      }

      Optional<TypeEntity> fieldTypeOpt = typeFinder.apply(fieldTypeName);

      if (fieldTypeOpt.isPresent()) {
        TypeEntity fieldType = fieldTypeOpt.get();
        sb.append(write(fieldType, depth + 1, writtenTypes));
        sb.append(type.getName()).append(" o-- ").append(fieldType.getName()).append("\n");
      }
    }
    return sb.toString();
  }

  String write(TypeEntity type) {
    StringBuilder sb = new StringBuilder();
    sb.append("class ").append(type.getName()).append(" {\n");

    sb.append(type.getFields().stream().map(this::write).collect(Collectors.joining("\n")));

    sb.append(type.getMethods().stream().map(this::write).collect(Collectors.joining("\n")));

    sb.append("\n}\n");

    return sb.toString();
  }

  String write(FieldEntity field) {
    return field.getName() + " : " + SyntaxUtils.toSimpleType(field.getType());
  }

  String write(MethodEntity method) {
    if (StringUtils.contains(method.getAnnotations(), "lombok.Generated")) {
      return "";
    }

    return method.getName() + "() : " + SyntaxUtils.toSimpleType(method.getReturnType());
  }
}

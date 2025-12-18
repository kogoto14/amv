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
import org.apache.commons.lang3.Strings;

@RequiredArgsConstructor
public class ClassDiagramLogic {

  private final int maxRecursionDepth;

  private final Function<String, Optional<TypeEntity>> typeFinder;

  public DiagramVo generateClassDiagram(List<TypeEntity> types) {
    StringBuilder sb = new StringBuilder();
    sb.append("@startuml\n");
    sb.append("skinparam linetype ortho\n");

    Set<String> writtenTypes = new HashSet<>();

    for (TypeEntity type : types) {
      sb.append(writeDiagramRecursive(type, 0, writtenTypes));
    }

    sb.append("@enduml");
    return new DiagramVo(sb.toString());
  }

  String writeDiagramRecursive(TypeEntity type, int depth, Set<String> writtenTypes) {
    if (depth > maxRecursionDepth || writtenTypes.contains(type.getQualifiedName())) {
      return "";
    }

    writtenTypes.add(type.getQualifiedName());

    StringBuilder sb = new StringBuilder();

    sb.append(writeClass(type));

    for (var field : type.getFields()) {
      typeFinder
          .apply(extractTypeName(field.getType()))
          .ifPresent(
              fieldType -> {
                sb.append(writeDiagramRecursive(fieldType, depth + 1, writtenTypes));

                if (writtenTypes.contains(fieldType.getQualifiedName())) {
                  sb.append(writeRelation(type, fieldType));
                }
              });
    }
    return sb.toString();
  }

  String writeRelation(TypeEntity parent, TypeEntity child) {
    return parent.getName() + " o-- " + child.getName() + "\n";
  }

  String writeClass(TypeEntity type) {
    StringBuilder sb = new StringBuilder();
    sb.append("class ").append(type.getName()).append(" {\n");

    sb.append(type.getFields().stream().map(this::writeField).collect(Collectors.joining("\n")));

    sb.append(type.getMethods().stream().map(this::writeMethod).collect(Collectors.joining("\n")));

    sb.append("\n}\n");

    return sb.toString();
  }

  String writeField(FieldEntity field) {
    return field.getName() + " : " + SyntaxUtils.toSimpleType(field.getType());
  }

  String writeMethod(MethodEntity method) {
    if (Strings.CS.contains(method.getAnnotations(), "lombok.Generated")) {
      return "";
    }

    return method.getName() + "() : " + SyntaxUtils.toSimpleType(method.getReturnType());
  }

  String extractTypeName(String rawTypeName) {
    return Strings.CS.contains(rawTypeName, "<") && Strings.CS.contains(rawTypeName, ">")
        ? StringUtils.substringBetween(rawTypeName, "<", ">")
        : rawTypeName;
  }
}

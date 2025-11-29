package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.arch.util.MethodUtils;
import dev.aulait.amv.arch.util.SyntaxUtils;
import dev.aulait.amv.domain.process.MethodCallEntity;
import dev.aulait.amv.domain.process.MethodCallEntityId;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodParamEntity;
import dev.aulait.amv.domain.process.TypeEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Deprecated
@RequiredArgsConstructor
public class SequenceDiagramLogic {

  private final Function<String, Optional<TypeEntity>> typeResolver;
  private final Function<MethodCallEntityId, List<MethodCallEntity>> callerResolver;

  public SequenceDiagramVo generateSequenceDiagram(
      MethodEntity method, List<String> participableStereotypes) {

    StringBuilder sb = new StringBuilder();
    sb.append("@startuml\n");

    Context context = new Context();
    context.getParticipableStereotypes().addAll(participableStereotypes);

    List<MethodCallEntity> sortedCalls =
        method.getMethodCalls().stream()
            .sorted(Comparator.comparing(MethodCallEntity::getLineNo))
            .toList();

    for (MethodCallEntity call : sortedCalls) {
      sb.append(writeMessage(method, call, context));
    }

    sb.append("@enduml");

    return SequenceDiagramVo.builder()
        .diagram(new DiagramVo(sb.toString()))
        .participantStereotypes(context.getParticipantStereotypes())
        .paramOrReturnTypes(context.getParamOrReturnTypes())
        .build();
  }

  String writeMessage(MethodEntity callerMethod, MethodCallEntity call, Context context) {

    if (context.isWritten(call)) {
      return "";
    }

    if (call.getMethod() == null) {
      return "";
    }

    StringBuilder sb = new StringBuilder();

    callerResolver
        .apply(call.getId())
        .forEach(callerCall -> sb.append(writeMessage(callerMethod, callerCall, context)));

    TypeEntity callerType = typeResolver.apply(callerMethod.getId().getTypeId()).orElseThrow();
    MethodEntity calleeMethod = call.getMethod();
    TypeEntity calleeType = typeResolver.apply(calleeMethod.getId().getTypeId()).orElseThrow();

    if (!context.isParticipable(calleeType)) {
      context.add(call, callerType, calleeType);
      return "";
    }

    sb.append(callerType.getName())
        .append(" -> ")
        .append(calleeType.getName())
        .append(" : ")
        .append(MethodUtils.formatSignatureWithLineBreaks(calleeMethod))
        .append("\n")
        .append("activate ")
        .append(calleeType.getName())
        .append("\n");

    context.add(call, callerType, calleeType);
    context.addParamOrReturnType(callerMethod.getReturnType());
    callerMethod.getMethodParams().stream()
        .map(MethodParamEntity::getType)
        .forEach(context::addParamOrReturnType);

    calleeMethod.getMethodCalls().stream()
        .sorted(Comparator.comparing(MethodCallEntity::getLineNo))
        .map(calleeCall -> writeMessage(calleeMethod, calleeCall, context))
        .forEach(sb::append);

    sb.append(callerType.getName())
        .append(" <-- ")
        .append(calleeType.getName())
        .append(" : ")
        .append(SyntaxUtils.toSimpleType(calleeMethod.getReturnType()))
        .append("\n")
        .append("deactivate ")
        .append(calleeType.getName())
        .append("\n");

    return sb.toString();
  }

  @Data
  class Context {
    List<String> participableStereotypes = new ArrayList<>();
    Set<MethodCallEntity> written = new HashSet<>();
    Set<String> participants = new LinkedHashSet<>();
    Set<String> paramOrReturnTypes = new HashSet<>();

    boolean isWritten(MethodCallEntity call) {
      return written.contains(call);
    }

    boolean isParticipable(TypeEntity type) {
      if (participableStereotypes.isEmpty()) {
        return true;
      }

      String stereotype = SyntaxUtils.extractStereotype(type.getName());
      return participableStereotypes.contains(stereotype);
    }

    void add(MethodCallEntity call, TypeEntity callerType, TypeEntity calleeType) {
      written.add(call);
      participants.add(callerType.getName());
      participants.add(calleeType.getName());
    }

    void addParamOrReturnType(String type) {
      paramOrReturnTypes.add(type);
    }

    Set<String> getParticipantStereotypes() {
      return participants.stream()
          .map(SyntaxUtils::extractStereotype)
          .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    }
  }
}

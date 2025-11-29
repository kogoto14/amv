package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.arch.util.MethodUtils;
import dev.aulait.amv.arch.util.SyntaxUtils;
import dev.aulait.amv.domain.process.MethodCallEntity;
import dev.aulait.amv.domain.process.MethodCallEntityId;
import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.TypeEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Strings;

@RequiredArgsConstructor
public class SequenceDiagramLogic2 {

  private final Function<String, Optional<TypeEntity>> typeResolver;
  private final Function<MethodCallEntityId, List<MethodCallEntity>> callerResolver;

  public SequenceDiagramVo generate(MethodEntity method, List<String> participableStereotypes) {

    List<MessageAggregate> messages = build(method);

    Set<String> participantStereotypes = new LinkedHashSet<>();
    Set<String> paramOrReturnTypes = new LinkedHashSet<>();
    for (MessageAggregate message : messages) {
      participantStereotypes.addAll(message.getParticipantStereotypes());
      paramOrReturnTypes.addAll(message.getParamOrReturnTypes());
    }

    return SequenceDiagramVo.builder()
        .diagram(new DiagramVo(write(messages, participableStereotypes)))
        .participantStereotypes(participantStereotypes)
        .paramOrReturnTypes(paramOrReturnTypes)
        .build();
  }

  public List<MessageAggregate> build(MethodEntity method) {
    List<MessageAggregate> messages = new ArrayList<>();
    BuildContext context = new BuildContext();

    List<MethodCallEntity> sortedCalls =
        method.getMethodCalls().stream()
            .sorted(Comparator.comparing(MethodCallEntity::getLineNo))
            .toList();

    ParticipantAggregate firstParticipant =
        context.getOrCreateParticipant(
            typeResolver.apply(method.getId().getTypeId()).orElseThrow());

    MessageAggregate firstMessage =
        MessageAggregate.builder().to(firstParticipant).method(method).depth(0).build();
    messages.add(firstMessage);

    for (MethodCallEntity call : sortedCalls) {
      messages.addAll(buildMessage(method, call, context, 0));
    }

    MessageAggregate returnOfFirstMessage =
        MessageAggregate.builder()
            .to(firstParticipant)
            .method(method)
            .depth(0)
            .returnMessage(true)
            .build();
    messages.add(returnOfFirstMessage);

    return messages;
  }

  List<MessageAggregate> buildMessage(
      MethodEntity callerMethod, MethodCallEntity call, BuildContext context, int depth) {
    if (context.contains(call)) {
      return List.of();
    }

    if (call.getMethod() == null) {
      return List.of();
    }

    List<MessageAggregate> messages = new ArrayList<>();

    callerResolver
        .apply(call.getId())
        .forEach(
            callerCall ->
                messages.addAll(buildMessage(callerMethod, callerCall, context, depth + 1)));

    TypeEntity callerType = typeResolver.apply(callerMethod.getId().getTypeId()).orElseThrow();
    MethodEntity calleeMethod = call.getMethod();
    TypeEntity calleeType = typeResolver.apply(calleeMethod.getId().getTypeId()).orElseThrow();

    ParticipantAggregate callerParticipant = context.getOrCreateParticipant(callerType);
    ParticipantAggregate calleeParticipant = context.getOrCreateParticipant(calleeType);

    MessageAggregate message =
        MessageAggregate.builder()
            .from(callerParticipant)
            .to(calleeParticipant)
            .method(call.getMethod())
            .depth(depth)
            .build();
    messages.add(message);
    context.add(call);

    calleeMethod.getMethodCalls().stream()
        .sorted(Comparator.comparing(MethodCallEntity::getLineNo))
        .map(calleeCall -> buildMessage(calleeMethod, calleeCall, context, depth + 1))
        .forEach(messages::addAll);

    MessageAggregate returnMessage =
        MessageAggregate.builder()
            .from(callerParticipant)
            .to(calleeParticipant)
            .method(call.getMethod())
            .depth(depth)
            .returnMessage(true)
            .build();
    messages.add(returnMessage);

    return messages;
  }

  @Data
  class BuildContext {
    Set<MethodCallEntity> methodCalls = new HashSet<>();
    Map<TypeEntity, ParticipantAggregate> participants = new LinkedHashMap<>();

    void add(MethodCallEntity call) {
      methodCalls.add(call);
    }

    boolean contains(MethodCallEntity call) {
      return methodCalls.contains(call);
    }

    ParticipantAggregate getOrCreateParticipant(TypeEntity type) {
      return participants.computeIfAbsent(type, ParticipantAggregate::new);
    }
  }

  public String write(List<MessageAggregate> messages, List<String> participableStereotypes) {
    StringBuilder sb = new StringBuilder();

    sb.append("@startuml\n");

    for (MessageAggregate message : messages) {
      if (!writeable(message, participableStereotypes)) {
        continue;
      }

      if (message.isReturnMessage()
          && Strings.CS.equals(message.getMethod().getReturnType(), "void")) {
        continue;
      }

      sb.append(depth(message))
          .append(participant(message.getFrom()))
          .append(arrow(message))
          .append(participant(message.getTo()))
          .append(" : ")
          .append(messageText(message))
          .append("\n")
          .append(activationKeyword(message))
          .append(participant(message.getTo()));

      sb.append("\n");
    }

    sb.append("@enduml\n");

    return sb.toString();
  }

  private boolean writeable(MessageAggregate message, List<String> participableStereotypes) {
    if (participableStereotypes.isEmpty()) {
      return true;
    }

    String fromStereotype = message.getFromStereotype();
    String toStereotype = message.getToStereotype();

    if (fromStereotype.isEmpty() || toStereotype.isEmpty()) {
      return true;
    }

    return participableStereotypes.contains(fromStereotype)
        && participableStereotypes.contains(toStereotype);
  }

  private String participant(ParticipantAggregate participant) {
    return participant == null ? "" : participant.getType().getName();
  }

  private String depth(MessageAggregate message) {
    return "  ".repeat(message.getDepth());
  }

  private String arrow(MessageAggregate message) {
    if (message.isReturnMessage()) {
      return " <-- ";
    } else if (message.isAsync()) {
      return " ->> ";
    } else {
      return " -> ";
    }
  }

  private String messageText(MessageAggregate message) {
    return message.isReturnMessage()
        ? SyntaxUtils.toSimpleType(message.getMethod().getReturnType())
        : MethodUtils.formatSignatureWithLineBreaks(message.getMethod());
  }

  private String activationKeyword(MessageAggregate message) {
    return message.isReturnMessage() ? "deactivate " : "activate ";
  }
}

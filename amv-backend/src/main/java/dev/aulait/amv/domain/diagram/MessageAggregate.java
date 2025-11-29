package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.domain.process.MethodEntity;
import dev.aulait.amv.domain.process.MethodParamEntity;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
public class MessageAggregate {
  private ParticipantAggregate from;
  private ParticipantAggregate to;
  private MethodEntity method;
  private int depth;
  private boolean async;
  private boolean returnMessage;

  @Getter(lazy = true)
  private final String fromStereotype = from == null ? "" : from.getStereotype();

  @Getter(lazy = true)
  private final String toStereotype = to == null ? "" : to.getStereotype();

  public Set<String> getParamOrReturnTypes() {
    Set<String> types = new HashSet<>();

    types.add(method.getReturnType());
    method.getMethodParams().stream().map(MethodParamEntity::getType).forEach(types::add);

    return types;
  }

  public Set<String> getParticipantStereotypes() {
    Set<String> stereotypes = new HashSet<>();

    if (from != null) {
      stereotypes.add(from.getStereotype());
    }

    if (to != null) {
      stereotypes.add(to.getStereotype());
    }

    return stereotypes;
  }
}

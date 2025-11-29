package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.arch.util.SyntaxUtils;
import dev.aulait.amv.domain.process.TypeEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ParticipantAggregate {
  private final TypeEntity type;
  private List<MessageAggregate> messages = new ArrayList<>();

  @Getter(lazy = true)
  private final String stereotype =
      getType() == null ? "" : SyntaxUtils.extractStereotype(getType().getName());
}

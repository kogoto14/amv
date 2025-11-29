package dev.aulait.amv.domain.diagram;

import dev.aulait.amv.arch.util.DiagramUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
public class DiagramVo {
  private final String text;

  @Getter(lazy = true)
  private String image = DiagramUtils.draw(getText());

  public static DiagramVo empty() {
    return new DiagramVo("");
  }
}

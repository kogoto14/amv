package dev.aulait.amv.interfaces.diagram;

import lombok.Data;

@Data
public class CallTreeCriteriaDto {
  private String signaturePattern;

  private boolean callTreeRequired = true;

  private boolean calledTreeRequired = false;

  private String render = "HTML";

  private int limit = 5;
}

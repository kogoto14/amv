package dev.aulait.amv.domain.extractor.java;

public enum FlowStatementKind {
  IF("01"),
  ELSE_IF("02"),
  ELSE("03"),

  FOR("10"),

  RETURN("20"),

  TRY("30"),
  CATCH("31"),
  FINALLY("32");

  private final String code;

  FlowStatementKind(String code) {
    this.code = code;
  }

  public String code() {
    return code;
  }
}

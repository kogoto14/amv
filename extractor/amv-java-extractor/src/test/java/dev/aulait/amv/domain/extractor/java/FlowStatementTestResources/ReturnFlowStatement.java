package dev.aulait.amv.domain.extractor.java.FlowStatementTestResources;

public class ReturnFlowStatement {

  public String returnStatement() {
    return innerRtnMethod();
  }

  public String returnNestedStatement() {
    boolean flg = true;
    if (flg) {
      return innerRtnMethod();
    }
    return defaultRtnMethod();
  }

  private String innerRtnMethod() {
    return "flow statement";
  }

  private String defaultRtnMethod() {
    return "default";
  }
}

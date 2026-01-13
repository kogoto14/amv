package dev.aulait.amv.domain.extractor.java.FlowStatementTestResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IfRelatedFlowStatements {

  public void ifFlowStatement() {
    boolean flg = true;
    if (flg) {
      innerMethod();
    } else if (flg) {
      innerMethod();
    } else {
      innerMethod();
    }
  }

  public void duplicateCaseFlowStatement() {
    List<String> readTestList = new ArrayList<>(Arrays.asList("testVal1", "testVal2", "testVal3"));
    if (readTestList.isEmpty()) {
      List.of();
    }

    List<String> inputTestList = new ArrayList<>();

    for (String testVal : readTestList) {
      inputTestList.add(testVal.toString());
    }
  }

  public void ifNestedFlowStatement() {
    boolean flg = true;
    if (flg) {
      flg = false;
      if (flg) {
        if (flg) {
          innerMethod();
        }
        innerMethod();
      } else {
        innerMethod();
      }
    } else if (flg) {
      innerMethod();
    } else {
      innerMethod();
    }
  }

  private void innerMethod() {
    System.out.println("flow statement");
  }
}

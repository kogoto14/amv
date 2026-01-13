package dev.aulait.amv.domain.extractor.java.FlowStatementTestResources;

import java.util.ArrayList;
import java.util.List;

public class ForRelatedFlowStatements {

  public void forRelatedStatement() {
    List<Integer> list = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      list.add(i);
    }

    for (Integer num : list) {
      System.out.println(num);
    }
  }

  public void forNestedFlowStatement() {
    List<Integer> list = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      for (Integer num : list) {
        System.out.println(i + num);
      }
    }
  }
}

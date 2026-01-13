package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.domain.extractor.fdo.FlowStatementFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AmvVisitorContext {
  @Getter private List<TypeFdo> typeList = new ArrayList<>();

  private Deque<TypeFdo> typeStack = new ArrayDeque<>();

  private Deque<MethodFdo> methodStack = new ArrayDeque<>();

  private Deque<MethodCallFdo> methodCallStack = new ArrayDeque<>();

  private Deque<FlowStatementFdo> flowStatementStack = new ArrayDeque<>();

  public void pushType(TypeFdo type) {
    typeStack.push(type);
    typeList.add(type);
  }

  public void popType() {
    typeStack.pop();
  }

  public TypeFdo getCurrentType() {
    return typeStack.peek();
  }

  public void pushMethod(MethodFdo method) {
    methodStack.push(method);
  }

  public void popMethod() {
    methodStack.pop();
  }

  public MethodFdo getCurrentMethod() {
    return methodStack.peek();
  }

  public void pushMethodCall(MethodCallFdo methodCall) {
    methodCallStack.push(methodCall);
  }

  public void popMethodCall() {
    methodCallStack.pop();
  }

  public MethodCallFdo getCurrentMethodCall() {
    return methodCallStack.peek();
  }

  public void pushFlowStatement(FlowStatementFdo flowStatement) {
    flowStatementStack.push(flowStatement);
  }

  public void popFlowStatement() {
    flowStatementStack.pop();
  }

  public FlowStatementFdo getCurrentFlowStatement() {
    return flowStatementStack.peek();
  }

  public void registerFlowStatement(FlowStatementFdo flow) {
    MethodFdo m = getCurrentMethod();
    if (m == null) {
      return;
    }
    if (m.getFlowStatements() == null) {
      m.setFlowStatements(new ArrayList<>());
    }
    m.getFlowStatements().add(flow);
  }
}

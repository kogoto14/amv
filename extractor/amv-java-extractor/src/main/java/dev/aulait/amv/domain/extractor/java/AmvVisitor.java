package dev.aulait.amv.domain.extractor.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.aulait.amv.domain.extractor.fdo.FieldFdo;
import dev.aulait.amv.domain.extractor.fdo.FlowStatementFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class AmvVisitor extends VoidVisitorAdapter<AmvVisitorContext> {

  @Builder.Default private MetadataConverter converter = new MetadataConverter();
  @Builder.Default private List<MetadataAdjuster> adjusters = new ArrayList<>();

  @Override
  public void visit(ClassOrInterfaceDeclaration n, AmvVisitorContext context) {
    TypeFdo type = converter.convert(n);

    context.pushType(type);
    super.visit(n, context);

    adjusters.forEach(adjuster -> adjuster.adjust(type));

    context.popType();
  }

  @Override
  public void visit(EnumDeclaration n, AmvVisitorContext context) {
    TypeFdo type = converter.convert(n);

    context.pushType(type);
    super.visit(n, context);
    context.popType();
  }

  @Override
  public void visit(FieldDeclaration n, AmvVisitorContext context) {
    FieldFdo field = converter.convert(n);
    context.getCurrentType().getFields().add(field);
    super.visit(n, context);
  }

  @Override
  public void visit(MethodDeclaration n, AmvVisitorContext context) {
    MethodFdo method = converter.convert(n);
    context.getCurrentType().getMethods().add(method);

    context.pushMethod(method);
    super.visit(n, context);
    context.popMethod();
  }

  @Override
  public void visit(MethodCallExpr n, AmvVisitorContext context) {
    MethodCallFdo methodCall = converter.convert(n);

    MethodFdo currentMethod = context.getCurrentMethod();
    // currentMethod is null if it's called from inside a static constructor.
    if (currentMethod != null) {
      currentMethod.getMethodCalls().add(methodCall);
    }

    methodCall.setId(UUID.randomUUID().toString());
    MethodCallFdo currentMethodCall = context.getCurrentMethodCall();
    if (currentMethodCall != null) {
      methodCall.setCallerId(currentMethodCall.getId());
    }

    FlowStatementFdo currentFlowStatement = context.getCurrentFlowStatement();
    if (currentFlowStatement != null) {
      methodCall.setFlowStatement(currentFlowStatement);
    }

    context.pushMethodCall(methodCall);
    super.visit(n, context);
    context.popMethodCall();
  }

  @Override
  public void visit(MethodReferenceExpr n, AmvVisitorContext context) {
    MethodCallFdo methodCall = converter.convert(n);

    MethodFdo currentMethod = context.getCurrentMethod();
    // currentMethod is null if it's called from inside a static constructor.
    if (currentMethod != null) {
      currentMethod.getMethodCalls().add(methodCall);
    }

    super.visit(n, context);
  }

  @Override
  public void visit(IfStmt n, AmvVisitorContext context) {
    FlowStatementFdo ifFlow = converter.convert(n);

    FlowStatementFdo parent = context.getCurrentFlowStatement();
    if (parent != null) {
      ifFlow.setParent(parent);
    }
    context.registerFlowStatement(ifFlow);

    context.pushFlowStatement(ifFlow);

    n.getCondition().accept(this, context);
    n.getThenStmt().accept(this, context);

    n.getElseStmt()
        .ifPresent(
            elseStmt -> {
              if (elseStmt.isIfStmt()) {
                elseStmt.asIfStmt().accept(this, context);
              } else {
                FlowStatementFdo elseFlow = converter.convert(elseStmt);
                elseFlow.setParent(ifFlow);

                context.registerFlowStatement(elseFlow);
                context.pushFlowStatement(elseFlow);
                elseStmt.accept(this, context);
                context.popFlowStatement();
              }
            });

    context.popFlowStatement();
  }

  @Override
  public void visit(ForStmt n, AmvVisitorContext context) {
    FlowStatementFdo forFlow = converter.convert(n);

    FlowStatementFdo parent = context.getCurrentFlowStatement();
    if (parent != null) {
      forFlow.setParent(parent);
    }
    context.registerFlowStatement(forFlow);

    context.pushFlowStatement(forFlow);
    super.visit(n, context);
    context.popFlowStatement();
  }

  @Override
  public void visit(ForEachStmt n, AmvVisitorContext context) {
    FlowStatementFdo forFlowStatement = converter.convert(n);
    FlowStatementFdo currentFlowStatement = context.getCurrentFlowStatement();
    if (currentFlowStatement != null) {
      forFlowStatement.setParent(currentFlowStatement);
    }
    context.registerFlowStatement(forFlowStatement);

    context.pushFlowStatement(forFlowStatement);
    super.visit(n, context);
    context.popFlowStatement();
  }

  @Override
  public void visit(ReturnStmt n, AmvVisitorContext context) {
    FlowStatementFdo returnFlow = converter.convert(n);
    FlowStatementFdo parent = context.getCurrentFlowStatement();

    if (parent != null) {
      returnFlow.setParent(parent);
    }
    context.registerFlowStatement(returnFlow);

    context.pushFlowStatement(returnFlow);
    super.visit(n, context);
    context.popFlowStatement();
  }

  @Override
  public void visit(TryStmt n, AmvVisitorContext context) {
    FlowStatementFdo tryFlow = converter.convert(n);
    FlowStatementFdo parent = context.getCurrentFlowStatement();
    if (parent != null) {
      tryFlow.setParent(parent);
    }

    context.registerFlowStatement(tryFlow);

    context.pushFlowStatement(tryFlow);
    super.visit(n, context);
    context.popFlowStatement();

    for (CatchClause cc : n.getCatchClauses()) {
      FlowStatementFdo catchFlow = converter.convert(cc);
      catchFlow.setParent(parent);

      context.registerFlowStatement(catchFlow);
      context.pushFlowStatement(catchFlow);
      super.visit(cc, context);
      context.popFlowStatement();
    }

    n.getFinallyBlock()
        .ifPresent(
            fb -> {
              FlowStatementFdo finallyFlow = converter.convert(fb);
              finallyFlow.setParent(parent);

              context.registerFlowStatement(finallyFlow);

              context.pushFlowStatement(finallyFlow);
              super.visit(fb, context);
              context.popFlowStatement();
            });
  }
}

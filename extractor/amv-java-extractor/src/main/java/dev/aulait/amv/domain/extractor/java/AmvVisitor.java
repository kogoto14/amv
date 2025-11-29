package dev.aulait.amv.domain.extractor.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.aulait.amv.domain.extractor.fdo.FieldFdo;
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

    context.pushMethodCall(methodCall);
    super.visit(n, context);
    context.popMethodCall();

    // adjusters.forEach(adjuster -> adjuster.adjust(n, methodCall));
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
}

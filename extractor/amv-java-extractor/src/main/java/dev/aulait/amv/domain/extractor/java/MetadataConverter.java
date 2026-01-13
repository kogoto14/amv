package dev.aulait.amv.domain.extractor.java;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import dev.aulait.amv.domain.extractor.fdo.AnnotationFdo;
import dev.aulait.amv.domain.extractor.fdo.FieldFdo;
import dev.aulait.amv.domain.extractor.fdo.FlowStatementFdo;
import dev.aulait.amv.domain.extractor.fdo.InheritedTypeFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.ParameterFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.util.Optional;

public class MetadataConverter {

  void load(TypeDeclaration<?> dec, TypeFdo fdo) {
    fdo.setName(dec.getNameAsString());
    fdo.setQualifiedName(JavaParserUtils.resolveTypeName(dec));
    fdo.setKind(type2kind(dec));

    dec.getAnnotations().stream().map(this::convert).forEach(fdo.getAnnotations()::add);
  }

  TypeFdo convert(EnumDeclaration dec) {
    TypeFdo fdo = new TypeFdo();
    load(dec, fdo);
    return fdo;
  }

  TypeFdo convert(ClassOrInterfaceDeclaration dec) {
    TypeFdo fdo = new TypeFdo();
    load(dec, fdo);

    dec.getExtendedTypes().stream().map(this::convert).forEach(fdo.getExtendedTypes()::add);
    dec.getImplementedTypes().stream().map(this::convert).forEach(fdo.getImplementedTypes()::add);

    return fdo;
  }

  InheritedTypeFdo convert(ClassOrInterfaceType type) {
    InheritedTypeFdo fdo = new InheritedTypeFdo();
    fdo.setName(type.getNameAsString());
    fdo.setQualifiedName(JavaParserUtils.resolve(type));

    if (type.getTypeArguments() != null && type.getTypeArguments().isPresent()) {
      type.getTypeArguments().get().stream()
          .map(JavaParserUtils::resolveType)
          .forEach(fdo.getTypeArguments()::add);
    }

    return fdo;
  }

  String type2kind(TypeDeclaration<?> type) {
    return type.isClassOrInterfaceDeclaration()
        ? (((ClassOrInterfaceDeclaration) type).isInterface() ? "interface" : "class")
        : (type instanceof EnumDeclaration
            ? "enum"
            : (type instanceof AnnotationDeclaration
                ? "@interface"
                : (type instanceof RecordDeclaration ? "record" : "type")));
  }

  public MethodFdo convert(MethodDeclaration dec) {
    MethodFdo fdo = new MethodFdo();
    fdo.setName(dec.getNameAsString());
    dec.getBegin().ifPresent(p -> fdo.setLineNo(p.line));

    MethodResolutionResult result = JavaParserUtils.resolve(dec);
    if (result.isResolved()) {

      fdo.setQualifiedSignature(result.getQualifiedSignature());

      if (result.isAbstract()) {
        fdo.setInterfaceSignature(result.getQualifiedSignature());
      }

    } else {
      fdo.setFallbackSignature(result.getFallbackSignature());
      fdo.setUnsolvedReason(result.getUnsolvedReason());
    }

    JavaParserUtils.resolveOverridingMethodSignature(dec).ifPresent(fdo::setInterfaceSignature);

    fdo.setReturnType(JavaParserUtils.resolveType(dec.getType()));

    dec.getParameters().stream().map(this::convert).forEach(fdo.getParameters()::add);

    dec.getAnnotations().stream().map(this::convert).forEach(fdo.getAnnotations()::add);
    return fdo;
  }

  ParameterFdo convert(Parameter param) {
    ParameterFdo fdo = new ParameterFdo();
    fdo.setName(param.getNameAsString());
    fdo.setType(JavaParserUtils.resolveType(param.getType()));
    return fdo;
  }

  AnnotationFdo convert(AnnotationExpr expr) {
    AnnotationFdo fdo = new AnnotationFdo();

    fdo.setQualifiedName(JavaParserUtils.resolveQualifiedName(expr));

    fdo.setAttributes(JavaParserUtils.anno2map(expr));

    return fdo;
  }

  public FieldFdo convert(FieldDeclaration dec) {
    FieldFdo fdo = new FieldFdo();
    fdo.setName(dec.getVariables().get(0).getNameAsString());
    fdo.setType(JavaParserUtils.resolveType(dec.getElementType()));

    return fdo;
  }

  public MethodCallFdo convert(MethodCallExpr expr) {
    MethodCallFdo fdo = new MethodCallFdo();
    expr.getBegin().ifPresent(p -> fdo.setLineNo(p.line));

    MethodResolutionResult result = JavaParserUtils.resolve(expr);

    if (result.isResolved()) {
      fdo.setQualifiedSignature(result.getQualifiedSignature());
      fdo.setInterfaceSignature(result.getInterfaceSignature());
    } else {
      fdo.setFallbackSignature(result.getFallbackSignature());
      fdo.setUnsolvedReason(result.getUnsolvedReason());
    }

    expr.getArguments().stream()
        .map(JavaParserUtils::resolveType)
        .forEach(fdo.getArgumentTypes()::add);

    return fdo;
  }

  public MethodCallFdo convert(MethodReferenceExpr expr) {
    MethodCallFdo fdo = new MethodCallFdo();
    expr.getBegin().ifPresent(p -> fdo.setLineNo(p.line));

    Optional<String> qualifiedSignature = JavaParserUtils.resolveQualifiedSignature(expr);
    if (qualifiedSignature.isPresent()) {
      fdo.setQualifiedSignature(qualifiedSignature.get());
    } else {
      fdo.setFallbackSignature(JavaParserUtils.fallbackMethodRefSig(expr));
    }

    return fdo;
  }

  public FlowStatementFdo convert(IfStmt n) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    n.getBegin().ifPresent(p -> fdo.setLineNo(p.line));

    boolean isElseIf =
        n.getParentNode()
            .filter(p -> p instanceof IfStmt)
            .map(p -> (IfStmt) p)
            .flatMap(IfStmt::getElseStmt)
            .map(elseStmt -> elseStmt == n)
            .orElse(false);

    if (isElseIf) {
      fdo.setKind(FlowStatementKind.ELSE_IF.code());
    } else {
      fdo.setKind(FlowStatementKind.IF.code());
    }

    fdo.setContent(n.getCondition().toString());
    return fdo;
  }

  public FlowStatementFdo convert(Statement stmt) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    stmt.getBegin().ifPresent(p -> fdo.setLineNo(p.line));
    if (stmt instanceof BlockStmt) {
      fdo.setKind(FlowStatementKind.ELSE.code());
      fdo.setContent("else");
    }
    return fdo;
  }

  public FlowStatementFdo convert(ForStmt f) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    f.getBegin().ifPresent(p -> fdo.setLineNo(p.line));
    fdo.setKind(FlowStatementKind.FOR.code());
    String content = f.getCompare().map(Expression::toString).orElse("");

    fdo.setContent(content);
    return fdo;
  }

  public FlowStatementFdo convert(ForEachStmt f) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    f.getBegin().ifPresent(p -> fdo.setLineNo(p.line));
    fdo.setKind(FlowStatementKind.FOR.code());

    String content = f.getVariable().toString() + " : " + f.getIterable().toString();

    fdo.setContent(content);
    return fdo;
  }

  public FlowStatementFdo convert(ReturnStmt n) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    n.getBegin().ifPresent(p -> fdo.setLineNo(p.line));
    fdo.setKind(FlowStatementKind.RETURN.code());

    fdo.setContent(n.getExpression().map(Expression::toString).orElse("void"));
    return fdo;
  }

  public FlowStatementFdo convert(TryStmt n) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    n.getBegin().ifPresent(p -> fdo.setLineNo(p.line));
    fdo.setKind(FlowStatementKind.TRY.code());
    fdo.setContent("try");
    return fdo;
  }

  public FlowStatementFdo convert(CatchClause n) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    n.getBegin().ifPresent(p -> fdo.setLineNo(p.line));
    fdo.setKind(FlowStatementKind.CATCH.code());

    String exception = n.getParameter().toString();
    fdo.setContent("catch (" + exception + ")");
    return fdo;
  }

  public FlowStatementFdo convert(BlockStmt block) {
    FlowStatementFdo fdo = new FlowStatementFdo();
    block.getBegin().ifPresent(p -> fdo.setLineNo(p.line));
    fdo.setKind(FlowStatementKind.FINALLY.code());

    fdo.setContent("finally");
    return fdo;
  }
}

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
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import dev.aulait.amv.domain.extractor.fdo.AnnotationFdo;
import dev.aulait.amv.domain.extractor.fdo.FieldFdo;
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
}

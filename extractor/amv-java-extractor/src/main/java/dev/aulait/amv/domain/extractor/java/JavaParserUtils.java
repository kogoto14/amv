package dev.aulait.amv.domain.extractor.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public class JavaParserUtils {

  public static JavaParser buildParser(
      List<Path> sourceDirs, String classpath, String javaVersion) {
    CombinedTypeSolver combinedSolver = new CombinedTypeSolver();

    TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
    combinedSolver.add(reflectionTypeSolver);

    sourceDirs.stream().map(JavaParserTypeSolver::new).forEach(combinedSolver::add);

    buildJarTypeSolver(classpath).forEach(combinedSolver::add);

    ParserConfiguration config =
        new ParserConfiguration()
            .setSymbolResolver(new JavaSymbolSolver(combinedSolver))
            .setLanguageLevel(toLanguageLevel(javaVersion));

    return new JavaParser(config);
  }

  public static List<JarTypeSolver> buildJarTypeSolver(String classpath) {
    return Arrays.stream(classpath.split(java.io.File.pathSeparator))
        .map(String::trim)
        .filter(s -> s.endsWith(".jar"))
        .map(
            jarPath -> {
              try {
                return new JarTypeSolver(jarPath);
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            })
        .toList();
  }

  static LanguageLevel toLanguageLevel(String javaVersion) {
    try {
      if (StringUtils.startsWith(javaVersion, "1.")) {
        String v = StringUtils.substringAfter(javaVersion, "1.");
        return LanguageLevel.valueOf("JAVA_" + v);
      } else {
        return LanguageLevel.valueOf("JAVA_" + javaVersion);
      }
    } catch (Exception e) {
      log.warn("Unknown Java version: {}, defaulting to JAVA_17", javaVersion);
      return LanguageLevel.JAVA_17;
    }
  }

  public static Optional<CompilationUnit> parse(JavaParser parser, Path sourceFile) {
    log.debug("Parsing {}", sourceFile);
    try {
      Optional<CompilationUnit> cu = parser.parse(sourceFile).getResult();

      if (cu.isEmpty()) {
        log.debug("No compilation unit found for {}", sourceFile);
      }

      return cu;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static String resolveType(Type type) {
    try {
      return type.resolve().describe();
    } catch (Throwable e) {
      return type.asString();
    }
  }

  public static MethodResolutionResult resolve(MethodDeclaration dec) {
    try {
      ResolvedMethodDeclaration r = dec.resolve();
      return MethodResolutionResult.builder()
          .qualifiedSignature(r.getQualifiedSignature())
          .isAbstract(r.isAbstract())
          .build();
    } catch (Throwable t) {
      return MethodResolutionResult.builder()
          .unsolvedReason(ExceptionUtils.getStackTrace(t))
          .fallbackSignature(fallbackMethodSig(dec))
          .build();
    }
  }

  public static Optional<String> resolveMethodCallSignature(MethodCallExpr n) {
    try {
      ResolvedMethodDeclaration r = n.resolve();
      return Optional.of(r.getQualifiedSignature());
    } catch (Throwable t) {
      return Optional.empty();
    }
  }

  public static MethodResolutionResult resolve(MethodCallExpr expr) {
    try {
      ResolvedMethodDeclaration dec = expr.resolve();
      String qualifiedSignature = dec.getQualifiedSignature();
      Optional<Expression> scopeOpt = expr.getScope();

      if (scopeOpt.isEmpty()) {
        return MethodResolutionResult.builder().qualifiedSignature(qualifiedSignature).build();
      }

      String scopeType = expr.getScope().get().calculateResolvedType().describe();

      if (qualifiedSignature.startsWith(scopeType + ".")) {

        if (dec.isAbstract()) {
          return MethodResolutionResult.builder()
              .qualifiedSignature(qualifiedSignature)
              .interfaceSignature(qualifiedSignature)
              .build();
        } else {
          return MethodResolutionResult.builder().qualifiedSignature(qualifiedSignature).build();
        }
      }

      return MethodResolutionResult.builder()
          .qualifiedSignature(scopeType + "." + dec.getSignature())
          .interfaceSignature(qualifiedSignature)
          .build();
    } catch (Throwable t) {
      return MethodResolutionResult.builder()
          .unsolvedReason(ExceptionUtils.getStackTrace(t))
          .fallbackSignature(fallbackMethodCallSig(expr))
          .build();
    }
  }

  public static Optional<String> resolveMethodOwnerType(MethodCallExpr n) {
    try {
      return Optional.of(n.resolve().declaringType().getQualifiedName());
    } catch (Throwable t) {
      return Optional.empty();
    }
  }

  public static Optional<String> resolveMethodRefSignature(MethodReferenceExpr n) {
    try {
      ResolvedMethodDeclaration r = n.resolve();
      return Optional.of(r.getQualifiedSignature());
    } catch (Throwable t) {
      return Optional.of(n.getIdentifier() + "::(unresolved)");
    }
  }

  public static Optional<String> resolveNewCtorSignature(ObjectCreationExpr n) {
    try {
      ResolvedConstructorDeclaration r = n.resolve();
      return Optional.of(r.getQualifiedSignature());
    } catch (Throwable t) {
      return Optional.of(n.getTypeAsString() + "(ctor ?)");
    }
  }

  public static String resolveExplicitCtorSignature(ExplicitConstructorInvocationStmt n) {
    try {
      ResolvedConstructorDeclaration r = n.resolve();
      return r.getQualifiedSignature();
    } catch (Throwable t) {
      return n.isThis() ? "this(...)" : "super(...)";
    }
  }

  public static String resolveQualifiedName(AnnotationExpr n) {
    try {
      return n.resolve().getQualifiedName();
    } catch (Throwable t) {
      return n.getNameAsString();
    }
  }

  public static String fallbackMethodSig(MethodDeclaration n) {
    // package + class can be derived from CU; we keep it simple
    String cls =
        n.findAncestor(ClassOrInterfaceDeclaration.class)
            .map(c -> c.getNameAsString())
            .orElse("<?>");
    return cls + "." + n.getNameAsString() + "(" + n.getParameters() + ")";
  }

  public static String fallbackMethodCallSig(MethodCallExpr n) {
    return n.getNameAsString() + "(" + n.getArguments() + ")";
  }

  public static String resolveType(Expression expr) {
    try {
      return expr.calculateResolvedType().describe();
    } catch (Throwable t) {
      return expr.toString();
    }
  }

  public static String resolveTypeName(TypeDeclaration<?> dec) {
    return dec.getFullyQualifiedName().orElse(dec.getNameAsString());
  }

  public static String resolve(ClassOrInterfaceType type) {
    try {
      return type.resolve().describe();
    } catch (Throwable e) {
      return type.asString();
    }
  }

  public static Map<String, Object> anno2map(AnnotationExpr anno) {
    if (anno.isMarkerAnnotationExpr()) {
      return Collections.emptyMap();
    }

    if (anno.isSingleMemberAnnotationExpr()) {
      SingleMemberAnnotationExpr s = anno.asSingleMemberAnnotationExpr();
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("value", exprToObject(s.getMemberValue()));
      return m;
    }

    if (anno.isNormalAnnotationExpr()) {
      NormalAnnotationExpr n = anno.asNormalAnnotationExpr();
      Map<String, Object> m = new LinkedHashMap<>();
      n.getPairs().forEach(p -> m.put(p.getNameAsString(), exprToObject(p.getValue())));
      return m;
    }

    return Collections.emptyMap(); // should not happen
  }

  public static Object exprToObject(Expression expr) {
    if (expr.isStringLiteralExpr()) return expr.asStringLiteralExpr().asString();
    if (expr.isCharLiteralExpr()) return expr.asCharLiteralExpr().asChar();
    if (expr.isBooleanLiteralExpr()) return expr.asBooleanLiteralExpr().getValue();
    if (expr.isIntegerLiteralExpr()) {
      try {
        return Integer.valueOf(expr.asIntegerLiteralExpr().getValue());
      } catch (NumberFormatException ex) {
        return expr.toString();
      }
    }
    if (expr.isLongLiteralExpr()) {
      String raw = expr.asLongLiteralExpr().getValue().replaceAll("[lL]$", "");
      try {
        return Long.valueOf(raw);
      } catch (NumberFormatException ex) {
        return expr.toString();
      }
    }
    if (expr.isDoubleLiteralExpr()) {
      try {
        return Double.valueOf(expr.asDoubleLiteralExpr().getValue());
      } catch (NumberFormatException ex) {
        return expr.toString();
      }
    }
    if (expr.isArrayInitializerExpr()) {
      ArrayInitializerExpr arr = expr.asArrayInitializerExpr();
      return arr.getValues().stream()
          .map(JavaParserUtils::exprToObject)
          .collect(Collectors.toList());
    }
    if (expr.isClassExpr()) { // e.g., String.class
      return expr.asClassExpr().getTypeAsString() + ".class";
    }
    if (expr.isFieldAccessExpr()) { // often enum constants like Color.RED
      return expr.asFieldAccessExpr().toString();
    }
    if (expr.isNameExpr()) { // bare enum constant imported statically, or constant name
      return expr.asNameExpr().getNameAsString();
    }
    if (expr.isEnclosedExpr()) {
      return exprToObject(expr.asEnclosedExpr().getInner());
    }
    if (expr.isUnaryExpr()) { // e.g., -42
      UnaryExpr u = expr.asUnaryExpr();
      Object inner = exprToObject(u.getExpression());
      return u.getOperator().asString() + inner;
    }
    if (expr.isAnnotationExpr()) { // nested annotation -> nested map
      return anno2map(expr.asAnnotationExpr());
    }

    // Fallback: keep source text (covers method calls, binary exprs, etc., if they appear)
    return clean(expr.toString());
  }

  private static String clean(String str) {
    String cleaned = str;

    if (StringUtils.contains(str, "\" + \"")) {
      cleaned = StringUtils.replace(str, "\" + \"", "");
    }

    if (StringUtils.startsWith(cleaned, "\"") && StringUtils.endsWith(cleaned, "\"")) {
      cleaned = StringUtils.substring(cleaned, 1, cleaned.length() - 1);
    }

    return cleaned;
  }

  public static Optional<String> resolveQualifiedSignature(MethodReferenceExpr expr) {
    try {
      ResolvedMethodDeclaration r = expr.resolve();
      return Optional.of(r.getQualifiedSignature());
    } catch (Throwable t) {
      return Optional.empty();
    }
  }

  public static String fallbackMethodRefSig(MethodReferenceExpr expr) {
    return expr.getScope() + "::" + expr.getIdentifier();
  }

  public static Optional<String> resolveOverridingMethodSignature(MethodDeclaration method) {
    try {
      ResolvedMethodDeclaration resolvedMethod = method.resolve();
      List<ResolvedReferenceType> ancestors = resolvedMethod.declaringType().getAllAncestors();

      for (ResolvedReferenceType ancestor : ancestors) {

        if (!ancestor.getTypeDeclaration().isPresent()) {
          continue;
        }

        if (ancestor.getQualifiedName().equals("java.lang.Object")) {
          continue;
        }

        return ancestor.getTypeDeclaration().get().getAllMethods().stream()
            // TODO: parameter types may differ in boxing, generics, etc.
            .filter(m -> m.getSignature().equals(resolvedMethod.getSignature()))
            .map(MethodUsage::getQualifiedSignature)
            .findFirst();
      }

      return Optional.empty();
    } catch (Throwable t) {
      return Optional.empty();
    }
  }
}

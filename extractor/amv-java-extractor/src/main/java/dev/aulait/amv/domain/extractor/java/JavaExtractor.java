package dev.aulait.amv.domain.extractor.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class JavaExtractor implements MetadataExtractor {

  private JavaParser parser;

  private List<MetadataAdjuster> adjusters;

  @Override
  public Optional<SourceFdo> extract(Path sourceFile) {
    Optional<CompilationUnit> cuOpt = JavaParserUtils.parse(parser, sourceFile);

    if (cuOpt.isEmpty()) {
      return Optional.empty();
    }

    CompilationUnit cu = cuOpt.get();

    SourceFdo source = new SourceFdo();
    source.setFilePath2(sourceFile);

    String namespace = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
    source.setNamespace(namespace);

    AmvVisitor visitor = AmvVisitor.builder().adjusters(adjusters).build();
    AmvVisitorContext context = new AmvVisitorContext();
    visitor.visit(cu, context);
    source.setTypes(context.getTypeList());

    return Optional.of(source);
  }

  @Override
  public List<SourceFdo> getAdditionals() {
    return adjusters.stream().map(MetadataAdjuster::getAdditionals).flatMap(List::stream).toList();
  }
}

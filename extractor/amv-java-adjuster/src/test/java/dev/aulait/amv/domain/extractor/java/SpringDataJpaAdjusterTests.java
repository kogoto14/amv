package dev.aulait.amv.domain.extractor.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.arch.test.ResourceUtils;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SpringDataJpaAdjusterTests {
  final String SAVE_ALL_QUALIFIED_SIG =
      "dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources.BookRepository.saveAll(java.lang.Iterable)";

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void saveAllDeclarationTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookRepository.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodFdo saveAll =
        extractedSource.getTypes().get(0).getMethods().stream()
            .filter(method -> "saveAll".equals(method.getName()))
            .findFirst()
            .get();

    assertEquals(SAVE_ALL_QUALIFIED_SIG, saveAll.getQualifiedSignature());
  }

  @Test
  void saveAllReferenceTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "BookService.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    MethodCallFdo saveAllCall =
        extractedSource.getTypes().get(0).getMethods().get(0).getMethodCalls().get(0);

    assertEquals(SAVE_ALL_QUALIFIED_SIG, saveAllCall.getQualifiedSignature());
  }
}

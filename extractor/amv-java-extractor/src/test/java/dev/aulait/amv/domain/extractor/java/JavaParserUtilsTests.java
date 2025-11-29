package dev.aulait.amv.domain.extractor.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.aulait.amv.arch.test.ResourceUtils;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class JavaParserUtilsTests {

  MetadataExtractor extractor = ExtractionServiceFactory.buildMetadataExtractor4Test();

  @Test
  void resolveOverridingMethodSignatureTest() {
    Path sampleSourceFile = ResourceUtils.res2path(this, "ImplementingType.java");

    SourceFdo extractedSource = extractor.extract(sampleSourceFile).get();

    assertEquals(
        "dev.aulait.amv.domain.extractor.java.JavaParserUtilsTestResources.InterfaceType.interfaceMethod()",
        extractedSource.getTypes().get(0).getMethods().get(0).getInterfaceSignature());
  }
}

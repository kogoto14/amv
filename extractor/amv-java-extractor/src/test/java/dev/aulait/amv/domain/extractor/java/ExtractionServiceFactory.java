package dev.aulait.amv.domain.extractor.java;

import java.nio.file.Path;
import java.util.List;

public class ExtractionServiceFactory {

  public static ExtractionService build() {
    MetadataExtractorFactory factory = metadataExtractorFactory();
    return new ExtractionService(factory, new ExtractionLogic());
  }

  public static MetadataExtractorFactory metadataExtractorFactory() {
    return new JavaExtractorFactory(new MetadataAdjusterFactory());
  }

  public static MetadataExtractor buildMetadataExtractor4Test() {
    return metadataExtractorFactory()
        .build(
            Path.of(""),
            List.of(Path.of("src/test/java")),
            System.getProperty("java.class.path"),
            "21");
  }

  public static MetadataExtractor buildMetadataExtractor4Runtime() {
    return metadataExtractorFactory()
        .build(
            Path.of(""),
            List.of(Path.of("src/main/java")),
            System.getProperty("java.class.path"),
            "21");
  }
}

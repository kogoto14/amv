package dev.aulait.amv.domain.extractor.java;

import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JavaExtractorFactory implements MetadataExtractorFactory {

  private final MetadataAdjusterFactory adjusterFactory;

  @Override
  public JavaExtractor build(
      Path projectDir, List<Path> sourceDirs, String classpath, String javaVersion) {
    log.debug(
        "Building JavaExtractorJavaParserImpl with javaVersion: {}, sourceDirs: {} and classpath:"
            + " {}",
        javaVersion,
        sourceDirs,
        classpath);

    List<MetadataAdjuster> adjusters = adjusterFactory.getAdjusters();
    adjusters.forEach(adjuster -> adjuster.init(projectDir, classpath));

    return JavaExtractor.builder()
        .parser(JavaParserUtils.buildParser(sourceDirs, classpath, javaVersion))
        .adjusters(adjusters)
        .build();
  }
}

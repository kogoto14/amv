package dev.aulait.amv.domain.extractor.java;

import java.nio.file.Path;
import java.util.List;

public interface MetadataExtractorFactory {

  MetadataExtractor build(
      Path projectDir, List<Path> sourceDirs, String classpath, String languageVersion);
}

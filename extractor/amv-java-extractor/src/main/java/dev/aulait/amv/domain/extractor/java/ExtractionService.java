package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import dev.aulait.amv.domain.extractor.java.ExtractionLogic.ExtractionContext;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ExtractionService {

  private final MetadataExtractorFactory factory;

  private final ExtractionLogic logic;

  public Stream<SourceFdo> execute(
      Path projectDir,
      List<Path> sourceDirs,
      Path classpathFile,
      String globPattern,
      String languageVersion,
      Path outDir) {

    List<Path> targetFiles =
        sourceDirs.stream().flatMap(sourceDir -> FileUtils.walk(sourceDir, globPattern)).toList();

    log.info("Found {} files matching '{}'", targetFiles.size(), globPattern);

    if (targetFiles.isEmpty()) {
      return Stream.of();
    }

    final String classpath;

    if (Files.exists(classpathFile)) {
      classpath = FileUtils.read(classpathFile);
    } else {
      log.warn("Classpath file does not exist: {}", classpathFile);
      classpath = "";
    }

    Supplier<MetadataExtractor> supplier =
        () -> factory.build(projectDir, sourceDirs, classpath, languageVersion);

    ExtractorPool pool = new ExtractorPool(supplier);
    pool.fill(Runtime.getRuntime().availableProcessors());

    Stream<SourceFdo> additionalsStream =
        supplier.get().getAdditionals().stream()
            .filter(source -> logic.checkIfExtracted(outDir, source));

    Stream<SourceFdo> mainStream =
        targetFiles.parallelStream()
            .map(targetFile -> new ExtractionContext(targetFile, projectDir, pool.get(), outDir))
            .peek(logic::extract)
            .peek(context -> pool.put(context.extractor))
            .filter(logic::isExtracted)
            .peek(logic::adjustPath)
            .peek(logic::write)
            .map(logic::getSource);

    return Stream.concat(mainStream, additionalsStream);
  }

  @RequiredArgsConstructor
  class ExtractorPool {
    List<MetadataExtractor> extractors = new ArrayList<>();
    final Supplier<MetadataExtractor> supplier;

    void fill(int size) {
      for (int i = 0; i < size; i++) {
        extractors.add(supplier.get());
      }
    }

    synchronized MetadataExtractor get() {
      if (extractors.isEmpty()) {
        var extractor = supplier.get();
        extractors.add(extractor);
        return extractor;
      } else {
        return extractors.remove(0);
      }
    }

    synchronized void put(MetadataExtractor extractor) {
      extractors.add(extractor);
    }
  }
}

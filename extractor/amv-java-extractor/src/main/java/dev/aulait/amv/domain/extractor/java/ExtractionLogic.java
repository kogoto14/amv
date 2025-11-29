package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.arch.file.FileUtils;
import dev.aulait.amv.arch.util.JsonUtils;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtractionLogic {

  public synchronized boolean checkIfExtracted(Path outDir, SourceFdo source) {
    Path lockFile = outDir.getParent().resolve("additionals").resolve(source.getPath());

    if (Files.exists(lockFile)) {
      log.info("Additional source already exists, skipping: {}", lockFile);
      return false;
    }

    FileUtils.write(lockFile, "written as additionals");
    return true;
  }

  public ExtractionContext extract(ExtractionContext context) {
    context.extractor.extract(context.sourcePath).ifPresent(fdo -> context.source = fdo);
    return context;
  }

  public boolean isExtracted(ExtractionContext context) {
    return context.source != null;
  }

  public void adjustPath(ExtractionContext context) {
    String adjustedPath =
        context
            .projectDir
            .relativize(context.source.getFilePath2())
            .toString()
            .replace("_nolombok", "")
            .replace("_delombok", "");
    context.source.setPath(adjustedPath);
  }

  public void write(ExtractionContext context) {
    String json = JsonUtils.obj2fmtjson(context.source);
    Path outPath = Path.of(context.outDir.resolve(context.source.getPath()) + ".json");
    FileUtils.write(outPath, json);
  }

  public SourceFdo getSource(ExtractionContext context) {
    return context.source;
  }

  @RequiredArgsConstructor
  static class ExtractionContext {
    final Path projectDir;
    final MetadataExtractor extractor;
    final Path outDir;
    Path sourcePath;
    SourceFdo source;

    ExtractionContext setSourcePath(Path sourcePath) {
      this.sourcePath = sourcePath;
      return this;
    }

    public ExtractionContext(
        Path sourcePath, Path projectDir, MetadataExtractor extractor, Path outDir) {
      this(projectDir, extractor, outDir);
      this.sourcePath = sourcePath;
    }
  }
}

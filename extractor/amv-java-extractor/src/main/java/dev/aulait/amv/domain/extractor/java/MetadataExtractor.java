package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface MetadataExtractor {

  Optional<SourceFdo> extract(Path sourceFile);

  List<SourceFdo> getAdditionals();
}

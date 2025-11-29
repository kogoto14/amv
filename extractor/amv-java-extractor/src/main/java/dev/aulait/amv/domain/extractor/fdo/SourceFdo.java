package dev.aulait.amv.domain.extractor.fdo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SourceFdo {
  private Path filePath2;
  private String path;
  private String namespace;
  private List<TypeFdo> types = new ArrayList<>();
}

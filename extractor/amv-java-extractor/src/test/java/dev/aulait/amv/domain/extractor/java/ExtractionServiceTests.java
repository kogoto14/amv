package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.domain.project.ProjectDataFactory;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class ExtractionServiceTests {

  ExtractionService service = ExtractionServiceFactory.build();

  @Test
  void testExecute() {
    Path outDir = Path.of("../target/output");

    service.execute(
        ProjectDataFactory.AMV_BACK_DIR,
        ProjectDataFactory.AMV_BACK_SRC_DIRS,
        ProjectDataFactory.AMV_BACK_CLASSPATH_FILE,
        "**/*.java",
        "21",
        outDir);
  }
}

package dev.aulait.amv.domain.extractor.java;

import com.github.javaparser.ast.expr.MethodCallExpr;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.SourceFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.nio.file.Path;
import java.util.List;

public interface MetadataAdjuster {

  default void init(Path projectDir, String classpath) {
    // do nothing
  }

  void adjust(TypeFdo type);

  default List<SourceFdo> getAdditionals() {
    return List.of();
  }

  default void adjust(MethodCallExpr expr, MethodCallFdo fdo) {
    // do nothing
  }
}

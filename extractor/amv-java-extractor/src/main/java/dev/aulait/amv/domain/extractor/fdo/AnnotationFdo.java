package dev.aulait.amv.domain.extractor.fdo;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class AnnotationFdo {
  private String qualifiedName;
  private Map<String, Object> attributes = new HashMap<>();
}

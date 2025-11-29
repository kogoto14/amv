package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.domain.extractor.fdo.AnnotationFdo;
import dev.aulait.amv.domain.extractor.fdo.EntryPointFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class JaxRsAdjuster implements MetadataAdjuster {

  @Override
  public void adjust(TypeFdo type) {

    String basePath = extractPathIfExists(type.getAnnotations()).orElse("");

    type.getMethods().stream()
        .forEach(
            method ->
                adjust(method, basePath).ifPresent(entryPoint -> method.setEntryPoint(entryPoint)));
  }

  Optional<EntryPointFdo> adjust(MethodFdo method, String basePath) {
    Optional<EntryPointFdo> entryPoint =
        extractIfEntryPoint(method.getAnnotations())
            .map(
                ep -> {
                  String path = basePath + "/" + StringUtils.defaultString(ep.getPath());
                  ep.setPath(path);
                  return ep;
                });

    return entryPoint;
  }

  Optional<EntryPointFdo> extractIfEntryPoint(List<AnnotationFdo> annotations) {
    EntryPointFdo fdo = new EntryPointFdo();

    for (AnnotationFdo annotation : annotations) {
      String name = Objects.toString(annotation.getQualifiedName());

      switch (name) {
        case "jakarta.ws.rs.GET" -> fdo.setHttpMethod("GET");
        case "jakarta.ws.rs.POST" -> fdo.setHttpMethod("POST");
        case "jakarta.ws.rs.PUT" -> fdo.setHttpMethod("PUT");
        case "jakarta.ws.rs.DELETE" -> fdo.setHttpMethod("DELETE");
        case "jakarta.ws.rs.Path" -> fdo.setPath(extractPath(annotation));
      }
    }

    return fdo.getHttpMethod() == null ? Optional.empty() : Optional.of(fdo);
  }

  String extractPath(AnnotationFdo annotation) {
    Object path = annotation.getAttributes().get("value");
    if (path != null) {
      return path.toString();
    }
    return null;
  }

  Optional<String> extractPathIfExists(List<AnnotationFdo> annotations) {
    return annotations.stream()
        .filter(a -> StringUtils.equals(a.getQualifiedName(), "jakarta.ws.rs.Path"))
        .map(this::extractPath)
        .filter(Objects::nonNull)
        .findFirst();
  }
}

package dev.aulait.amv.domain.extractor.java;

import dev.aulait.amv.domain.extractor.fdo.AnnotationFdo;
import dev.aulait.amv.domain.extractor.fdo.EntryPointFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class SpringMvcAdjuster implements MetadataAdjuster {

  @Override
  public void adjust(TypeFdo type) {

    Optional<AnnotationFdo> requestMappingAnnotation =
        type.getAnnotations().stream()
            .filter(
                a ->
                    StringUtils.equals(
                        a.getQualifiedName(),
                        "org.springframework.web.bind.annotation.RequestMapping"))
            .findFirst();

    if (!requestMappingAnnotation.isPresent()) {
      return;
    }

    String basePath = extractPath(requestMappingAnnotation.get());

    type.getMethods().stream()
        .forEach(
            method ->
                extract(method, basePath)
                    .ifPresent(entryPoint -> method.setEntryPoint(entryPoint)));
  }

  Optional<EntryPointFdo> extract(MethodFdo method, String basePath) {
    return method.getAnnotations().stream()
        .map(this::extractIfEntryPoint)
        .filter(Objects::nonNull)
        .peek(
            entryPoint -> {
              String path = basePath + StringUtils.defaultString(entryPoint.getPath());
              entryPoint.setPath(path);
            })
        .findFirst();
  }

  EntryPointFdo extractIfEntryPoint(AnnotationFdo annotation) {
    EntryPointFdo fdo = new EntryPointFdo();

    String name = annotation.getQualifiedName();

    if (name.equals("org.springframework.web.bind.annotation.GetMapping")) {
      fdo.setHttpMethod("GET");
      fdo.setPath(extractPath(annotation));
    } else if (name.equals("org.springframework.web.bind.annotation.PostMapping")) {
      fdo.setHttpMethod("POST");
      fdo.setPath(extractPath(annotation));
    } else if (name.equals("org.springframework.web.bind.annotation.PutMapping")) {
      fdo.setHttpMethod("PUT");
      fdo.setPath(extractPath(annotation));
    } else if (name.equals("org.springframework.web.bind.annotation.DeleteMapping")) {
      fdo.setHttpMethod("DELETE");
      fdo.setPath(extractPath(annotation));
    } else if (name.equals("org.springframework.web.bind.annotation.RequestMapping")) {
      Object methodAttr = annotation.getAttributes().get("method");
      if (methodAttr != null) {
        String methodStr = methodAttr.toString();
        if (methodStr.contains("GET")) {
          fdo.setHttpMethod("GET");
        } else if (methodStr.contains("POST")) {
          fdo.setHttpMethod("POST");
        } else if (methodStr.contains("PUT")) {
          fdo.setHttpMethod("PUT");
        } else if (methodStr.contains("DELETE")) {
          fdo.setHttpMethod("DELETE");
        }
      }
      fdo.setPath(extractPath(annotation));
    }

    return fdo.getHttpMethod() == null ? null : fdo;
  }

  String extractPath(AnnotationFdo annotation) {
    Object path = annotation.getAttributes().get("value");
    if (path == null) {
      path = annotation.getAttributes().get("path");
    }
    if (path != null) {
      return path.toString();
    }
    return null;
  }
}

package dev.aulait.amv.domain.extractor.fdo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TypeFdo {
  private String name;
  private String qualifiedName;
  private String kind;
  private String dataKind;
  private String dataName;
  private List<InheritedTypeFdo> extendedTypes = new ArrayList<>();
  private List<InheritedTypeFdo> implementedTypes = new ArrayList<>();
  private List<MethodFdo> methods = new ArrayList<>();
  private List<FieldFdo> fields = new ArrayList<>();
  private List<AnnotationFdo> annotations = new ArrayList<>();
}

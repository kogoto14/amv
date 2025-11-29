package dev.aulait.amv.domain.extractor.fdo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class InheritedTypeFdo {
  private String name;
  private String qualifiedName;
  private String kind;
  private List<String> typeArguments = new ArrayList<>();
}

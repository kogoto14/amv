package dev.aulait.amv.domain.extractor.fdo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MethodFdo {
  private String name;
  private String qualifiedSignature;
  private String fallbackSignature;
  private String interfaceSignature;
  private String unsolvedReason;
  private String returnType;
  private int lineNo;
  private List<ParameterFdo> parameters = new ArrayList<>();
  private List<MethodCallFdo> methodCalls = new ArrayList<>();
  private List<AnnotationFdo> annotations = new ArrayList<>();
  private EntryPointFdo entryPoint;
  private List<CrudPointFdo> crudPoints = new ArrayList<>();
}

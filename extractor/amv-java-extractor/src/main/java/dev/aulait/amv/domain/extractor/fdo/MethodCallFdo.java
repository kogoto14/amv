package dev.aulait.amv.domain.extractor.fdo;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MethodCallFdo {
  private String qualifiedSignature;
  private String interfaceSignature;
  private String fallbackSignature;
  private String unsolvedReason;
  private int lineNo;
  private List<String> argumentTypes = new ArrayList<>();
  @EqualsAndHashCode.Include private String id;
  private String callerId;
}

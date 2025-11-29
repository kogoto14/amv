package dev.aulait.amv.domain.extractor.java;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Builder
@Value
public class MethodResolutionResult {
  private String qualifiedSignature;
  private String interfaceSignature;
  private boolean isAbstract;
  private String unsolvedReason;
  private String fallbackSignature;

  public boolean isResolved() {
    return StringUtils.isNotEmpty(qualifiedSignature);
  }
}

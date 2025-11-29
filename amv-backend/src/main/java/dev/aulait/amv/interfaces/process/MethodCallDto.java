package dev.aulait.amv.interfaces.process;

import lombok.Data;

@Data
public class MethodCallDto implements Comparable<MethodCallDto> {
  private String qualifiedSignature;
  private String interfaceSignature;
  private String fallbackSignature;
  private String unsolvedReason;
  private int lineNo;

  @Override
  public int compareTo(MethodCallDto o) {
    return Integer.compare(this.lineNo, o.lineNo);
  }
}

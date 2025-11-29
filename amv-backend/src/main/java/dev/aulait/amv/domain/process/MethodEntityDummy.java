package dev.aulait.amv.domain.process;

import java.util.Objects;

public class MethodEntityDummy extends MethodEntity {

  public static MethodEntity of(MethodCallEntity methodCall) {
    if (methodCall.getMethod() != null) {
      return methodCall.getMethod();
    }

    String qualifiedSignature =
        Objects.toString(methodCall.getQualifiedSignature(), methodCall.getFallbackSignature());

    return MethodEntityDummy.builder().name(qualifiedSignature).build();
  }
}

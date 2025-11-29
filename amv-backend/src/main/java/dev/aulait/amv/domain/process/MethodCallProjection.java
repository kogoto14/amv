package dev.aulait.amv.domain.process;

public interface MethodCallProjection {
  MethodEntity getMethod();

  MethodCallEntity getMethodCall();
}

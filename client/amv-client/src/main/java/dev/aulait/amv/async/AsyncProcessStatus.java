package dev.aulait.amv.client.async;

public enum AsyncProcessStatus {
  RUNNING,
  COMPLETED,
  FAILED,
  UNKNOWN;

  public static AsyncProcessStatus from(String raw) {
    if (raw == null || raw.isBlank()) {
      return UNKNOWN;
    }
    try {
      return AsyncProcessStatus.valueOf(raw.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      return UNKNOWN;
    }
  }

  public boolean isRunning() {
    return this == RUNNING;
  }

  public boolean isSuccessed() {
    return this == COMPLETED;
  }

  public boolean isFailed() {
    return this == FAILED || this == UNKNOWN;
  }
}

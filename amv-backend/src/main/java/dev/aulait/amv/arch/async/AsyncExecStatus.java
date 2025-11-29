package dev.aulait.amv.arch.async;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AsyncExecStatus {
  RUNNING(false),
  COMPLETED(true),
  FAILED(true),
  UNKNOWN(true);

  @Getter private boolean isFinished = false;

  public static boolean isFinished(String status) {
    return AsyncExecStatus.valueOf(status).isFinished();
  }
}
